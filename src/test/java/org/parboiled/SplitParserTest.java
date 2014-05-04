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

package org.parboiled;

import org.parboiled.annotations.BuildParseTree;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

public class SplitParserTest extends ParboiledTest<Object>
{
    /*
     * This one is intersting... It basically means that parsers are
     * "reentrant".
     *
     * Not that surprising since BaseParser implements ContextAware; but still,
     * this is an interesting trick!
     */
    @BuildParseTree
    public static class Parser extends BaseParser<Object> {
        final Primitives primitives = Parboiled.createParser(Primitives.class);

        public Rule Clause() {
            return sequence(
                    primitives.digit(),
                    primitives.Operator(),
                    primitives.digit(),
                    EOI
            );
        }
    }

    @BuildParseTree
    public static class Primitives extends BaseParser<Object> {

        public Rule Operator() {
            return firstOf('+', '-');
        }

    }

    @Test
    public void test() {
        Parser parser = Parboiled.createParser(Parser.class);
        test(parser.Clause(), "1+5")
                .hasNoErrors()
                .hasParseTree("" +
                        "[Clause] '1+5'\n" +
                        "  [digit] '1'\n" +
                        "  [Operator] '+'\n" +
                        "    ['+'] '+'\n" +
                        "  [digit] '5'\n" +
                        "  [EOI]\n");
    }

}