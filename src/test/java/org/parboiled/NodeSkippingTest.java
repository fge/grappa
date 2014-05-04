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
import org.parboiled.annotations.SkipNode;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

public class NodeSkippingTest extends ParboiledTest<Object>
{

    @BuildParseTree
    public static class Parser extends BaseParser<Object> {

        public Rule ABCDEFGH() {
            return sequence(ABCD(), EFGH());
        }

        public Rule ABCD() {
            return sequence(AB(), CD());
        }

        public Rule EFGH() {
            return sequence(EF(), GH()).skipNode();
        }

        public Rule AB() {
            return sequence(A(), B());
        }

        @SkipNode
        public Rule CD() {
            return sequence(C(), D());
        }

        public Rule EF() {
            return sequence(E(), F());
        }

        @SkipNode
        public Rule GH() {
            return sequence(G(), H()).skipNode();
        }

        public Rule A() {
            return ch('a');
        }

        public Rule B() {
            return ch('b').skipNode();
        }

        public Rule C() {
            return ch('c');
        }

        public Rule D() {
            return ch('d');
        }

        public Rule E() {
            return ch('e');
        }

        public Rule F() {
            return ch('f');
        }

        public Rule G() {
            return ch('g');
        }

        public Rule H() {
            return ch('h');
        }

        public Rule BugIn101() {
            return firstOf(
                    sequence("a", "c").skipNode(),
                    "a"
            );
        }
    }

    @Test
    public void testNodeSuppression() {
        Parser parser = Parboiled.createParser(Parser.class);
        test(parser.ABCDEFGH(), "abcdefgh")
                .hasNoErrors()
                .hasParseTree("" +
                        "[ABCDEFGH] 'abcdefgh'\n" +
                        "  [ABCD] 'abcd'\n" +
                        "    [AB] 'ab'\n" +
                        "      [A] 'a'\n" +
                        "    [C] 'c'\n" +
                        "    [D] 'd'\n" +
                        "  [EF] 'ef'\n" +
                        "    [E] 'e'\n" +
                        "    [F] 'f'\n" +
                        "  [G] 'g'\n" +
                        "  [H] 'h'\n");
        test(parser.BugIn101(), "abc")
                .hasNoErrors()
                .hasParseTree("" +
                        "[BugIn101] 'a'\n" +
                        "  ['a'] 'a'\n");
    }

}