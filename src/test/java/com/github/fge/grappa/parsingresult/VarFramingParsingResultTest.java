/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.grappa.parsingresult;

import com.github.fge.grappa.testparsers.TestParser;
import com.github.fge.grappa.testparsers.VarFramingParser;
import org.parboiled.Rule;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.support.Var;

import java.io.IOException;

public final class VarFramingParsingResultTest
    extends ParsingResultTest<VarFramingParser, Integer>
{
    static class Parser
        extends TestParser<Integer>
    {
        int count = 1;

        @Override
        public Rule mainRule()
        {
            final Var<Integer> a = new Var<>(-1);
            return sequence(
                digits(),
                a.set(peek()),
                someRule(a),
                optional('+', mainRule(), push(a.get()))
            );
        }

        @SuppressNode
        public Rule digits()
        {
            return sequence(
                oneOrMore(digit()),
                push(Integer.parseInt(match()))
            );
        }

        public Rule someRule(final Var<Integer> var)
        {
            return toRule(var.get() == count++);
        }
    }


    public VarFramingParsingResultTest()
        throws IOException
    {
        super(VarFramingParser.class, "varFraming.json");
    }
}
