/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.transform.process;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.transform.InstructionGraphNode;
import org.parboiled.transform.InstructionGroup;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ALOAD;

public final class InstructionGroupPreparer
    implements RuleMethodProcessor
{

    private static final Map<Character, Character> TRANSFORM_MAP;
    /*
     * Note: we have to go through the map above; BaseEncoding will rightly
     * yell at you if you try and use a separator which is part of the alphabet.
     */
    private static final BaseEncoding BASE_ENCODING = BaseEncoding.base64();

    /*
     * TODO: rework that part
     *
     * The goal is to generate a suitable hash representable as a string
     * containing valid characters for Java identifiers.
     *
     * The current process has two major problems:
     *
     * - it uses MD5 as a hash and this has is not that good;
     * - it limits the output length to 96 bytes; probably to limit the length
     *   of the generated identifier;
     * - the MessageDigest instance is static (but final, now).
     *
     * Ideally a better hash should be used. But for this it is needed to get
     * rid of the "toString()" tests of bytecode!
     */
    static {
        final ImmutableMap.Builder<Character, Character> builder
            = ImmutableMap.builder();

        builder.put('z', '0');
        builder.put('0', '1');
        builder.put('1', '2');
        builder.put('2', '3');
        builder.put('3', '4');
        builder.put('4', '5');
        builder.put('5', '6');
        builder.put('6', '7');
        builder.put('7', '8');
        builder.put('8', '9');
        builder.put('9', 'z');
        builder.put('+', 'z');
        builder.put('/', 'z');

        TRANSFORM_MAP = builder.build();
    }

    private RuleMethod method;

    @Override
    public boolean appliesTo(final ParserClassNode classNode,
        final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.containsExplicitActions() || method.containsVars();
    }

    @Override
    public void process(final ParserClassNode classNode, final RuleMethod method)
    {
        this.method = Preconditions.checkNotNull(method, "method");

        // prepare groups for later stages
        for (final InstructionGroup group : method.getGroups()) {
            extractInstructions(group);
            extractFields(group);
            name(group, classNode);
        }
    }

    // move all group instructions except for the root from the underlying method into the groups Insnlist
    private void extractInstructions(final InstructionGroup group)
    {
        for (final InstructionGraphNode node : group.getNodes()) {
            if (node == group.getRoot())
                continue;
            final AbstractInsnNode insn = node.getInstruction();
            method.instructions.remove(insn);
            group.getInstructions().add(insn);
        }
    }

    // create FieldNodes for all xLoad instructions
    private static void extractFields(final InstructionGroup group)
    {
        final List<FieldNode> fields = group.getFields();
        for (final InstructionGraphNode node : group.getNodes()) {
            if (node.isXLoad()) {
                final VarInsnNode insn = (VarInsnNode) node.getInstruction();

                // check whether we already have a field for the var with this index
                int index;
                for (index = 0; index < fields.size(); index++) {
                    if (fields.get(index).access == insn.var)
                        break;
                }

                // if we don't, create a new field for the var
                if (index == fields.size()) {
                    /*
                     * CAUTION, HACK!: for brevity we reuse the access field and
                     * the value field of the FieldNode for keeping track of the
                     * original var index as well as the FieldNodes Type
                     * (respectively) so we need to make sure that we correct
                     * for this when the field is actually written
                     */
                    final Type type = node.getResultValue().getType();
                    fields.add(new FieldNode(insn.var, "field$" + index,
                        type.getDescriptor(), null, type));
                }

                // normalize the instruction so instruction groups that are identical except for the variable
                // indexes are still mapped to the same group class (name)
                insn.var = index;
            }
        }
    }

    // set a group name base on the hash across all group instructions and fields
    private synchronized void name(final InstructionGroup group,
        final ParserClassNode classNode)
    {
        // generate an MD5 hash across the buffer, use only the first 96 bit
        final MD5Digester digester = new MD5Digester(classNode.name);
        group.getInstructions().accept(digester);
        for (final FieldNode field : group.getFields())
            digester.visitField(field);
        final byte[] hash = Arrays.copyOf(digester.getMD5Hash(), 12);

        // generate a name for the group based on the hash
        String name = group.getRoot().isActionRoot() ? "Action$" : "VarInit$";
        name += illGuidedTransform(BASE_ENCODING.encode(hash));
        group.setName(name);
    }

    @Immutable
    private static final class MD5Digester
        extends MethodVisitor
    {
        private static final ByteBuffer BUFFER = ByteBuffer.allocate(4096);
        private static final MessageDigest DIGEST;

        static {
            try {
                DIGEST = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        private final List<Label> labels = new ArrayList<Label>();
        private final String parserClassName;

        private MD5Digester(final String parserClassName)
        {
            super(Opcodes.ASM4);
            this.parserClassName = parserClassName;
            BUFFER.clear();
        }

        @Override
        public void visitInsn(final int opcode)
        {
            update(opcode);
        }

        @Override
        public void visitIntInsn(final int opcode, final int operand)
        {
            update(opcode);
            update(operand);
        }

        @Override
        public void visitVarInsn(final int opcode, final int var)
        {
            update(opcode);
            update(var);
            if (opcode == ALOAD && var == 0) {
                // make sure the names of identical actions differ if they are defined in different parent classes
                update(parserClassName);
            }
        }

        @Override
        public void visitTypeInsn(final int opcode, final String type)
        {
            update(opcode);
            update(type);
        }

        @Override
        public void visitFieldInsn(
            final int opcode, final String owner, final String name,
            final String desc)
        {
            update(opcode);
            update(owner);
            update(name);
            update(desc);
        }

        @Override
        public void visitMethodInsn(
            final int opcode, final String owner, final String name,
            final String desc, final boolean itf)
        {
            update(opcode);
            update(owner);
            update(name);
            update(desc);
        }

        @Override
        public void visitJumpInsn(final int opcode, final Label label)
        {
            update(opcode);
            update(label);
        }

        @Override
        public void visitLabel(final Label label)
        {
            update(label);
        }

        @Override
        public void visitLdcInsn(final Object cst)
        {
            if (cst instanceof String) {
                update((String) cst);
            } else if (cst instanceof Integer) {
                update((Integer) cst);
            } else if (cst instanceof Float) {
                ensureRemaining(4);
                BUFFER.putFloat((Float) cst);
            } else if (cst instanceof Long) {
                ensureRemaining(8);
                BUFFER.putLong((Long) cst);
            } else if (cst instanceof Double) {
                ensureRemaining(8);
                BUFFER.putDouble((Double) cst);
            } else {
                Preconditions.checkState(cst instanceof Type);
                update(((Type) cst).getInternalName());
            }
        }

        @Override
        public void visitIincInsn(final int var, final int increment)
        {
            update(var);
            update(increment);
        }

        @Override
        public void visitTableSwitchInsn(
            final int min, final int max, final Label dflt,
            final Label[] labels)
        {
            update(min);
            update(max);
            update(dflt);
            for (final Label label : labels) {
                update(label);
            }
        }

        @Override
        public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
            final Label[] labels)
        {
            update(dflt);
            for (int i = 0; i < keys.length; i++) {
                update(keys[i]);
                update(labels[i]);
            }
        }

        @Override
        public void visitMultiANewArrayInsn(final String desc, final int dims)
        {
            update(desc);
            update(dims);
        }

        @Override
        public void visitTryCatchBlock(
            final Label start, final Label end, final Label handler,
            final String type)
        {
            update(start);
            update(end);
            update(handler);
            update(type);
        }

        private static void visitField(final FieldNode field)
        {
            update(field.name);
            update(field.desc);
            update(field.signature);
        }

        private static void update(final int i)
        {
            ensureRemaining(4);
            BUFFER.putInt(i);
        }

        private static void update(final String str)
        {
            if (Strings.isNullOrEmpty(str))
                return;
            final CharBuffer buf = CharBuffer.wrap(str);
            while (buf.hasRemaining())
                BUFFER.putChar(buf.get());
        }

        private void update(final Label label)
        {
            int index = labels.indexOf(label);
            if (index == -1) {
                index = labels.size();
                labels.add(label);
            }
            update(index);
        }

        private static void ensureRemaining(final int bytes)
        {
            if (BUFFER.remaining() < bytes)
                digest();
        }

        private static void digest()
        {
            BUFFER.flip();
            DIGEST.update(BUFFER);
            BUFFER.clear();
        }

        private static byte[] getMD5Hash()
        {
            digest();
            return DIGEST.digest();
        }
    }

    /*
     * Name says it all.
     */
    private static String illGuidedTransform(final String input)
    {
        final char[] orig = input.toCharArray();
        final CharBuffer buffer = CharBuffer.allocate(orig.length);

        for (final char c: orig)
            buffer.put(Optional.fromNullable(TRANSFORM_MAP.get(c)).or(c));

        return new String(buffer.array());
    }
}