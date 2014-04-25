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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.transform.InstructionGraphNode;
import org.parboiled.transform.InstructionGroup;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.parboiled.common.Preconditions.checkArgNotNull;
import static org.parboiled.transform.AsmUtils.getLoadingOpcode;

/**
 * Inserts action group class instantiation code at the groups respective placeholders.
 */
public class RuleMethodRewriter implements RuleMethodProcessor {

    private RuleMethod method;
    private InstructionGroup group;
    private int actionNr;
    private int varInitNr;

    @Override
    public boolean appliesTo(ParserClassNode classNode, RuleMethod method) {
        checkArgNotNull(classNode, "classNode");
        checkArgNotNull(method, "method");
        return method.containsExplicitActions() || method.containsVars();
    }

    @Override
    public void process(ParserClassNode classNode, RuleMethod method) throws Exception {
        this.method = checkArgNotNull(method, "method");
        actionNr = 0;
        varInitNr = 0;

        for (InstructionGroup group : method.getGroups()) {
            this.group = group;
            createNewGroupClassInstance();
            initializeFields();

            InstructionGraphNode root = group.getRoot();
            if (root.isActionRoot()) {
                removeGroupRootInstruction();
            } else { // if (root.isVarInitRoot())
                ((MethodInsnNode) root.getInstruction()).desc = "(Lorg/parboiled/common/Factory;)V";
            }
        }

        method.setBodyRewritten();
    }

    private void createNewGroupClassInstance() {
        String internalName = group.getGroupClassType().getInternalName();
        InstructionGraphNode root = group.getRoot();
        insert(new TypeInsnNode(NEW, internalName));
        insert(new InsnNode(DUP));
        insert(new LdcInsnNode(method.name +
                (root.isActionRoot() ? "_Action" + ++actionNr : "_VarInit" + ++varInitNr)));
        insert(new MethodInsnNode(INVOKESPECIAL, internalName, "<init>", "(Ljava/lang/String;)V"));

        if (root.isActionRoot() && method.hasSkipActionsInPredicatesAnnotation()) {
            insert(new InsnNode(DUP));
            insert(new MethodInsnNode(INVOKEVIRTUAL, internalName, "setSkipInPredicates", "()V"));
        }
    }

    private void initializeFields() {
        String internalName = group.getGroupClassType().getInternalName();
        for (FieldNode field : group.getFields()) {
            insert(new InsnNode(DUP));
            // the FieldNodes access and value members have been reused for the var index / Type respectively!
            insert(new VarInsnNode(getLoadingOpcode((Type) field.value), field.access));
            insert(new FieldInsnNode(PUTFIELD, internalName, field.name, field.desc));
        }
    }

    private void insert(AbstractInsnNode insn) {
        method.instructions.insertBefore(group.getRoot().getInstruction(), insn);
    }

    private void removeGroupRootInstruction() {
        method.instructions.remove(group.getRoot().getInstruction());
    }

}

