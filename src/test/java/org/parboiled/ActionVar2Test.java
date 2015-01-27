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

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.Reference;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

public class ActionVar2Test extends ParboiledTest<Object>
{

    // TODO: redo that test

    @BuildParseTree
    static class Parser extends BaseParser<Object>
    {
        Rule Clause() {
            final Reference<Integer> count = new Reference<>();
            return sequence(CharCount(count), Chars(count), '\n');
        }

        Rule CharCount(final Reference<Integer> count) {
            return sequence('{', oneOrMore(charRange('0', '9')), count.set(Integer.parseInt(match())), '}');
        }

        Rule Chars(final Reference<Integer> count) {
            return sequence(
                    zeroOrMore(EMPTY, count.get() > 0, ANY,
                        count.set(count.get() - 1)), count.get() == 0);
        }
    }

    @Test
    public void test() {
        final Parser parser = Parboiled.createParser(Parser.class);
        test(parser.Clause(), "{12}abcdefghijkl\n")
                .hasNoErrors()
                .hasParseTree("" +
                    "[Clause] '{12}abcdefghijkl\\n'\n" +
                    "  [CharCount] '{12}'\n" +
                    "    ['{'] '{'\n" +
                    "    [oneOrMore] '12'\n" +
                    "      [0..9] '1'\n" +
                    "      [0..9] '2'\n" +
                    "    ['}'] '}'\n" +
                    "  [Chars] 'abcdefghijkl'\n" +
                    "    [zeroOrMore] 'abcdefghijkl'\n" +
                    "      [sequence] 'a'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'a'\n" +
                    "      [sequence] 'b'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'b'\n" +
                    "      [sequence] 'c'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'c'\n" +
                    "      [sequence] 'd'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'd'\n" +
                    "      [sequence] 'e'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'e'\n" +
                    "      [sequence] 'f'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'f'\n" +
                    "      [sequence] 'g'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'g'\n" +
                    "      [sequence] 'h'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'h'\n" +
                    "      [sequence] 'i'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'i'\n" +
                    "      [sequence] 'j'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'j'\n" +
                    "      [sequence] 'k'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'k'\n" +
                    "      [sequence] 'l'\n" +
                    "        [EMPTY]\n" +
                    "        [ANY] 'l'\n" +
                    "  ['\\n'] '\\n'\n");
    }

}
