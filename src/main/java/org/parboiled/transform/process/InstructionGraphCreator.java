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
import com.google.common.base.Preconditions;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;
import org.parboiled.transform.RuleMethodInterpreter;

import javax.annotation.Nonnull;

/**
 * Performs data/control flow analysis and constructs the instructions graph.
 */
@WillBeFinal(version = "1.1")
public class InstructionGraphCreator
    implements RuleMethodProcessor
{
    @Override
    public boolean appliesTo(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
    {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.containsImplicitActions()
            || method.containsExplicitActions()
            || method.containsVars();
    }

    @Override
    public void process(@Nonnull final ParserClassNode classNode,
        @Nonnull final RuleMethod method)
        throws Exception
    {
        Preconditions.checkNotNull(method, "method");
        final RuleMethodInterpreter interpreter
            = new RuleMethodInterpreter(method);
        final RuleMethodAnalyzer analyzer = new RuleMethodAnalyzer(interpreter);
        analyzer.analyze(classNode.name, method);
        interpreter.finish();

    }

    // TODO: probably needs a rewrite; I have a hunch this is a gross hack
    private static final class RuleMethodAnalyzer
        extends Analyzer<BasicValue>
    {
        private final RuleMethodInterpreter rmi;

        private RuleMethodAnalyzer(final RuleMethodInterpreter rmi)
        {
            super(Preconditions.checkNotNull(rmi));
            this.rmi = rmi;
        }

        @Override
        protected void newControlFlowEdge(final int insn,
            final int successor)
        {
            rmi.newControlFlowEdge(insn, successor);
        }

        @Override
        protected boolean newControlFlowExceptionEdge(final int insn,
            final int successor)
        {
            rmi.newControlFlowEdge(insn, successor);
            return true;
        }
    }
}
