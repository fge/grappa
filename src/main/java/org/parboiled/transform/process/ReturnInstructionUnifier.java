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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GOTO;

/**
 * Replaces all "non-last" return instructions with goto instructions to the
 * last return instruction. If a method contains only one return instruction
 * the transformer does nothing.
 */
@WillBeFinal(version = "1.1")
// TODO: remove?
public class ReturnInstructionUnifier
    implements RuleMethodProcessor
{
    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        return method.getNumberOfReturns() > 1;
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        Preconditions.checkState(method.getNumberOfReturns() > 1);

        AbstractInsnNode current = method.instructions.getLast();

        // find last return
        while (current.getOpcode() != ARETURN)
            current = current.getPrevious();


        final LabelNode lastReturnLabel = new LabelNode();
        method.instructions.insertBefore(current, lastReturnLabel);

        // iterate backwards up to first instructions
        while ((current = current.getPrevious()) != null) {

            // replace returns with gotos
            if (current.getOpcode() != ARETURN)
                continue;

            final JumpInsnNode gotoInstruction
                = new JumpInsnNode(GOTO, lastReturnLabel);
            method.instructions.set(current, gotoInstruction);
            current = gotoInstruction;
        }
    }
}