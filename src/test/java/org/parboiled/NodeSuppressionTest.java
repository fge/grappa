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
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

public class NodeSuppressionTest extends ParboiledTest<Object>
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
            return sequence(EF(), GH());
        }

        public Rule AB() {
            return sequence(A(), B());
        }

        @SuppressSubnodes
        public Rule CD() {
            return sequence(C(), D());
        }

        public Rule EF() {
            return sequence(E(), F());
        }

        public Rule GH() {
            return sequence(G(), H()).suppressNode();
        }

        public Rule A() {
            return ch('a');
        }

        @SuppressNode
        public Rule B() {
            return ch('b');
        }

        public Rule C() {
            return ch('c');
        }

        public Rule D() {
            return ch('d');
        }

        public Rule E() {
            return ch('e').suppressSubnodes();
        }

        public Rule F() {
            return ch('f').suppressNode();
        }

        public Rule G() {
            return ch('g');
        }

        public Rule H() {
            return ch('h');
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
                        "    [CD] 'cd'\n" +
                        "  [EFGH] 'efgh'\n" +
                        "    [EF] 'ef'\n" +
                        "      [E] 'e'\n");
    }

}