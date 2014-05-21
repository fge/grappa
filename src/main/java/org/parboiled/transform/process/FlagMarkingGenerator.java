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
import me.qmx.jitescript.util.CodegenUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.parboiled.Rule;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * Adds the required flag marking calls before the return instruction.
 */
@WillBeFinal(version = "1.1")
public class FlagMarkingGenerator
    implements RuleMethodProcessor
{
    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.hasSuppressNodeAnnotation()
            || method.hasSuppressSubnodesAnnotation()
            || method.hasSkipNodeAnnotation()
            || method.hasMemoMismatchesAnnotation();
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        // super methods have flag moved to the overriding method
        Preconditions.checkState(!method.isSuperMethod());

        final InsnList instructions = method.instructions;

        AbstractInsnNode ret = instructions.getLast();
        while (ret.getOpcode() != ARETURN)
            ret = ret.getPrevious();


        // stack: <rule>
        instructions.insertBefore(ret, new InsnNode(DUP));
        // stack: <rule> :: <rule>
        final LabelNode isNullLabel = new LabelNode();
        instructions.insertBefore(ret, new JumpInsnNode(IFNULL, isNullLabel));
        // stack: <rule>

        if (method.hasSuppressNodeAnnotation())
            generateMarkerCall(instructions, ret, "suppressNode");
        if (method.hasSuppressSubnodesAnnotation())
            generateMarkerCall(instructions, ret, "suppressSubnodes");
        if (method.hasSkipNodeAnnotation())
            generateMarkerCall(instructions, ret, "skipNode");
        if (method.hasMemoMismatchesAnnotation())
            generateMarkerCall(instructions, ret, "memoMismatches");

        // stack: <rule>
        instructions.insertBefore(ret, isNullLabel);
        // stack: <rule>
    }

    private static void generateMarkerCall(final InsnList instructions,
        final AbstractInsnNode ret, final String call)
    {
        final MethodInsnNode insn = new MethodInsnNode(INVOKEINTERFACE,
            CodegenUtils.p(Rule.class), call,
            CodegenUtils.sig(Rule.class), true);
        instructions.insertBefore(ret, insn);
    }
}