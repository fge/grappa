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
import com.github.fge.grappa.misc.Reference;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

public class ActionVar2Test extends ParboiledTest<Object>
{

    // TODO: redo that test

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
        test(parser.Clause(), "{12}abcdefghijkl\n").hasNoErrors();
    }

}
