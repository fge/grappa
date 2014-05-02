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

import com.google.common.base.Preconditions;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.transform.InstructionGraphNode;
import org.parboiled.transform.InstructionGroup;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;
import org.parboiled.transform.Types;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.parboiled.transform.AsmUtils.findLoadedClass;
import static org.parboiled.transform.AsmUtils.loadClass;

public abstract class GroupClassGenerator implements RuleMethodProcessor {

    private static final Object lock = new Object();

    private final boolean forceCodeBuilding;
    protected ParserClassNode classNode;
    protected RuleMethod method;

    protected GroupClassGenerator(final boolean forceCodeBuilding) {
        this.forceCodeBuilding = forceCodeBuilding;
    }

    @Override
    public void process(final ParserClassNode classNode, final RuleMethod method) {
        this.classNode = Preconditions.checkNotNull(classNode, "classNode");
        this.method = Preconditions.checkNotNull(method, "method");

        for (final InstructionGroup group : method.getGroups()) {
            if (appliesTo(group.getRoot())) {
                loadGroupClass(group);
            }
        }
    }

    protected abstract boolean appliesTo(InstructionGraphNode group);

    private void loadGroupClass(final InstructionGroup group) {
        createGroupClassType(group);
        final String className = group.getGroupClassType().getClassName();
        final ClassLoader classLoader = classNode.getParentClass().getClassLoader();

        final Class<?> groupClass;
        synchronized (lock) {
            groupClass = findLoadedClass(className, classLoader);
            if (groupClass == null || forceCodeBuilding) {
                final byte[] groupClassCode = generateGroupClassCode(group);
                group.setGroupClassCode(groupClassCode);
                if (groupClass == null) {
                    loadClass(className, groupClassCode, classLoader);
                }
            }
        }
    }

    private void createGroupClassType(final InstructionGroup group) {
        final String s = classNode.name;
        final int lastSlash = classNode.name.lastIndexOf('/');
        final String groupClassInternalName = (lastSlash >= 0 ? s.substring(0, lastSlash) : s)+ '/' + group.getName();
        group.setGroupClassType(Type.getObjectType(groupClassInternalName));
    }

    protected byte[] generateGroupClassCode(final InstructionGroup group) {
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        generateClassBasics(group, classWriter);
        generateFields(group, classWriter);
        generateConstructor(classWriter);
        generateMethod(group, classWriter);
        return classWriter.toByteArray();
    }

    private void generateClassBasics(
        final InstructionGroup group, final ClassWriter cw) {
        cw.visit(Opcodes.V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SYNTHETIC,
            group.getGroupClassType().getInternalName(), null,
                getBaseType().getInternalName(), null);
        cw.visitSource(classNode.sourceFile, null);
    }

    protected abstract Type getBaseType();

    private void generateFields(final InstructionGroup group, final ClassWriter cw) {
        for (final FieldNode field : group.getFields()) {
            // CAUTION: the FieldNode has illegal access flags and an illegal value field since these two members
            // are reused for other purposes, so we need to write out the field "manually" here rather than
            // just call "field.accept(cw)"
            cw.visitField(ACC_PUBLIC + ACC_SYNTHETIC, field.name, field.desc, null, null);
        }
    }

    private void generateConstructor(final ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, getBaseType().getInternalName(),
            "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // trigger automatic computing
    }

    protected abstract void generateMethod(InstructionGroup group, ClassWriter cw);

    protected void insertSetContextCalls(final InstructionGroup group, int localVarIx) {
        final InsnList instructions = group.getInstructions();
        for (final InstructionGraphNode node : group.getNodes()) {
            if (node.isCallOnContextAware()) {
                final AbstractInsnNode insn = node.getInstruction();

                if (node.getPredecessors().size() > 1) {
                    // store the target of the call in a new local variable
                    final AbstractInsnNode loadTarget = node.getPredecessors().get(0).getInstruction();
                    instructions.insert(loadTarget, new VarInsnNode(ASTORE, ++localVarIx));
                    instructions.insert(loadTarget, new InsnNode(DUP)); // the DUP is inserted BEFORE the ASTORE

                    // immediately before the call get the target from the local var and set the context on it
                    instructions.insertBefore(insn, new VarInsnNode(ALOAD, localVarIx));
                } else {
                    // if we have only one predecessor the call does not take any parameters and we can
                    // skip the storing and loading of the invocation target
                    instructions.insertBefore(insn, new InsnNode(DUP));
                }
                instructions.insertBefore(insn, new VarInsnNode(ALOAD, 1));
                instructions.insertBefore(insn,
                    new MethodInsnNode(INVOKEINTERFACE,
                    Types.CONTEXT_AWARE.getInternalName(),
                        "setContext", "(" + Types.CONTEXT_DESC + ")V", true));
            }
        }
    }

    protected void convertXLoads(final InstructionGroup group) {
        final String owner = group.getGroupClassType().getInternalName();
        for (final InstructionGraphNode node : group.getNodes()) {
            if (!node.isXLoad()) continue;

            final VarInsnNode insn = (VarInsnNode) node.getInstruction();
            final FieldNode field = group.getFields().get(insn.var);

            // insert the correct GETFIELD after the xLoad
            group.getInstructions().insert(insn, new FieldInsnNode(GETFIELD, owner, field.name, field.desc));

            // change the load to ALOAD 0
            group.getInstructions().set(insn, new VarInsnNode(ALOAD, 0));
        }
    }

}