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

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.google.common.base.Preconditions;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * Replaces the method code with a simple call to the super method.
 */
@WillBeFinal(version = "1.1")
public class SuperCallRewriter
    implements RuleMethodProcessor
{
    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.containsPotentialSuperCalls();
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        final InsnList instructions = method.instructions;
        AbstractInsnNode insn = instructions.getFirst();
        while (insn.getOpcode() != ARETURN) {
            if (insn.getOpcode() == INVOKESPECIAL)
                process(classNode, method, (MethodInsnNode) insn);
            insn = insn.getNext();
        }
    }

    private void process(final ParserClassNode classNode,
        final RuleMethod method, final MethodInsnNode insn)
    {
        if ("<init>".equals(insn.name))
            return;
        final String superMethodName = getSuperMethodName(method, insn);
        final RuleMethod superMethod = classNode.getRuleMethods().get(
            superMethodName + insn.desc);
        if (superMethod == null)
            return;
        if (!superMethod.isBodyRewritten())
            return;

        // since the super method is rewritten we do need to generate it
        superMethod.dontSkipGeneration();

        // we have a call to a super method that was rewritten, so we need to
        // change the call to the generated method
        insn.setOpcode(INVOKEVIRTUAL);
        insn.name = superMethodName;
        insn.owner = classNode.name;

        method.setBodyRewritten();
    }

    private static String getSuperMethodName(final RuleMethod method,
        final MethodInsnNode insn)
    {
        Class<?> clazz = method.getOwnerClass();
        String superMethodName = method.name;
        do {
            clazz = clazz.getSuperclass();
            // we should find the owner before hitting Object
            Preconditions.checkState(clazz != null);
            superMethodName = '$' + superMethodName;
        } while (!Type.getInternalName(clazz).equals(insn.owner));
        return superMethodName;
    }
}