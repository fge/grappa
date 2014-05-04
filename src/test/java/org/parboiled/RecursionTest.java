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

public class RecursionTest extends ParboiledTest<Object>
{

    @BuildParseTree
    public static class Parser extends BaseParser<Object> {

        @SuppressWarnings("infiniteRecursion")
        public Rule LotsOfAs() {
            return sequence(ignoreCase('a'), optional(LotsOfAs()));
        }

    }

    @Test
    public void testRecursion() {
        Parser parser = Parboiled.createParser(Parser.class);
        test(parser.LotsOfAs(), "AaA")
                .hasNoErrors()
                .hasParseTree("" +
                        "[LotsOfAs] 'AaA'\n" +
                        "  ['a/A'] 'A'\n" +
                        "  [Optional] 'aA'\n" +
                        "    [LotsOfAs] 'aA'\n" +
                        "      ['a/A'] 'a'\n" +
                        "      [Optional] 'A'\n" +
                        "        [LotsOfAs] 'A'\n" +
                        "          ['a/A'] 'A'\n" +
                        "          [Optional]\n");
    }

}