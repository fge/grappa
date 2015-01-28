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

import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.transform.CodeBlock;
import com.google.common.base.Preconditions;
import me.qmx.jitescript.util.CodegenUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.ARETURN;

/**
 * Adds the required flag marking calls before the return instruction.
 */
public final class FlagMarkingGenerator
    implements RuleMethodProcessor
{
    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Objects.requireNonNull(classNode, "classNode");
        Objects.requireNonNull(method, "method");
        return method.hasSuppressNodeAnnotation()
            || method.hasSuppressSubnodesAnnotation()
            || method.hasSkipNodeAnnotation();
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        Objects.requireNonNull(classNode, "classNode");
        Objects.requireNonNull(method, "method");
        // super methods have flag moved to the overriding method
        Preconditions.checkState(!method.isSuperMethod());

        final InsnList instructions = method.instructions;

        AbstractInsnNode ret = instructions.getLast();
        while (ret.getOpcode() != ARETURN)
            ret = ret.getPrevious();

        final CodeBlock block = CodeBlock.newCodeBlock();

        final LabelNode label = new LabelNode();
        block.dup().ifnull(label);

        if (method.hasSuppressNodeAnnotation())
            block.invokeinterface(CodegenUtils.p(Rule.class), "suppressNode",
                CodegenUtils.sig(Rule.class));
        if (method.hasSuppressSubnodesAnnotation())
            block.invokeinterface(CodegenUtils.p(Rule.class),
                "suppressSubnodes", CodegenUtils.sig(Rule.class));
        if (method.hasSkipNodeAnnotation())
            block.invokeinterface(CodegenUtils.p(Rule.class), "skipNode",
                CodegenUtils.sig(Rule.class));

        block.label(label);
        instructions.insertBefore(ret, block.getInstructionList());
    }
}
