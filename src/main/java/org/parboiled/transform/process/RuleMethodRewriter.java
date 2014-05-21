/*
 * Copyright (c) 2009-2010 Ken Wenzel and Mathias Doenitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.parboiled.transform.process;

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.github.parboiled1.grappa.transform.asm.AsmHelper;
import com.google.common.base.Preconditions;
import me.qmx.jitescript.util.CodegenUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.common.Factory;
import org.parboiled.transform.InstructionGraphNode;
import org.parboiled.transform.InstructionGroup;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;

/**
 * Inserts action group class instantiation code at the groups respective
 * placeholders.
 */
@WillBeFinal(version = "1.1")
public class RuleMethodRewriter
    implements RuleMethodProcessor
{
    private RuleMethod method;
    private int actionNr = 0;
    private int varInitNr = 0;

    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.containsExplicitActions() || method.containsVars();
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        this.method = Preconditions.checkNotNull(method, "method");
        actionNr = 0;
        varInitNr = 0;

        for (final InstructionGroup group: method.getGroups()) {
            createNewGroupClassInstance(group);
            initializeFields(group);

            final InstructionGraphNode root = group.getRoot();
            if (root.isActionRoot()) {
                removeGroupRootInstruction(group);
            } else { // if (root.isVarInitRoot())
                // TODO: replace with Supplier
                ((MethodInsnNode) root.getInstruction()).desc
                    = CodegenUtils.sig(void.class, Factory.class);
            }
        }
        method.setBodyRewritten();
    }

    private void createNewGroupClassInstance(final InstructionGroup group)
    {
        final String internalName
            = group.getGroupClassType().getInternalName();
        final InstructionGraphNode root = group.getRoot();
        insert(group, new TypeInsnNode(NEW, internalName));
        insert(group, new InsnNode(DUP));
        insert(group, new LdcInsnNode(
            method.name + (root.isActionRoot() ? "_Action" + ++actionNr
                : "_VarInit" + ++varInitNr)));
        insert(group, new MethodInsnNode(INVOKESPECIAL, internalName, "<init>",
            CodegenUtils.sig(void.class, String.class), false));

        if (root.isActionRoot()
            && method.hasSkipActionsInPredicatesAnnotation()) {
            insert(group, new InsnNode(DUP));
            insert(group, new MethodInsnNode(INVOKEVIRTUAL, internalName,
                "setSkipInPredicates", CodegenUtils.sig(void.class), false));
        }
    }

    private void initializeFields(final InstructionGroup group)
    {
        final String internalName
            = group.getGroupClassType().getInternalName();
        for (final FieldNode field: group.getFields()) {
            insert(group, new InsnNode(DUP));
            // the FieldNodes access and value members have been reused for the
            // var index / Type respectively!
            final int opcode = AsmHelper.loadingOpcodeFor((Type) field.value);
            insert(group, new VarInsnNode(opcode, field.access));
            insert(group, new FieldInsnNode(PUTFIELD, internalName, field.name,
                field.desc));
        }
    }

    private void insert(final InstructionGroup group,
        final AbstractInsnNode insn)
    {
        method.instructions.insertBefore(group.getRoot().getInstruction(),
            insn);
    }

    private void removeGroupRootInstruction(final InstructionGroup group)
    {
        method.instructions.remove(group.getRoot().getInstruction());
    }
}

