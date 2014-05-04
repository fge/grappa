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

import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.google.common.base.Preconditions;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;

/**
 * Removes all unused labels.
 */
@WillBeFinal(version = "1.1")
// TODO: is it really useful?
public class UnusedLabelsRemover
    implements RuleMethodProcessor
{

    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        return true;
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        AbstractInsnNode current = method.instructions.getFirst();
        while (current != null) {
            final AbstractInsnNode next = current.getNext();
            if (current.getType() == AbstractInsnNode.LABEL
                && !method.getUsedLabels().contains(current))
                method.instructions.remove(current);
            current = next;
        }
    }
}