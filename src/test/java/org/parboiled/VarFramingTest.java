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
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.ProxyMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.VarFramingMatcher;
import org.parboiled.support.Var;
import org.parboiled.test.TestNgParboiledTest;
import com.github.parboiled1.grappa.assertions.StatsAssert;
import org.testng.annotations.Test;

public class VarFramingTest extends TestNgParboiledTest<Integer> {

    @BuildParseTree
    static class Parser extends BaseParser<Integer> {

        int count = 1;

        @SuppressWarnings( {"InfiniteRecursion"})
        public Rule Clause() {
            Var<Integer> a = new Var<Integer>(-1);
            return Sequence(
                    Digits(), a.set(peek()),
                    SomeRule(a),
                    Optional(
                            '+',
                            Clause(), push(a.get())
                    )
            );
        }

        @SuppressNode
        public Rule Digits() {
            return Sequence(
                    OneOrMore(CharRange('0', '9')),
                    push(Integer.parseInt(match()))
            );
        }

        public Rule SomeRule(Var<Integer> var) {
            return toRule(var.get() == count++);
        }

    }

    @Test
    public void test() {
        Parser parser = Parboiled.createParser(Parser.class);
        Rule rule = parser.Clause();

        StatsAssert.assertStatsForRule(rule)
            .hasCountedTotal(11)
            .hasCounted(1, CharMatcher.class)
            .hasCounted(1, CharRangeMatcher.class)
            .hasCounted(1, OneOrMoreMatcher.class)
            .hasCounted(1, OptionalMatcher.class)
            .hasCounted(3, SequenceMatcher.class)
            .hasCounted(1, ProxyMatcher.class)
            .hasCounted(1, VarFramingMatcher.class)
            .hasCountedActionClasses(4).hasCountedActions(4)
            .hasCountedNothingElse();

        test(rule, "1+2+3")
                .hasNoErrors()
                .hasParseTree("" +
                        "[Clause, {1}] '1+2+3'\n" +
                        "  [Optional, {1}] '+2+3'\n" +
                        "    [Sequence, {1}] '+2+3'\n" +
                        "      ['+', {1}] '+'\n" +
                        "      [Clause, {2}] '2+3'\n" +
                        "        [Optional, {2}] '+3'\n" +
                        "          [Sequence, {2}] '+3'\n" +
                        "            ['+', {2}] '+'\n" +
                        "            [Clause, {3}] '3'\n" +
                        "              [Optional, {3}]\n");
    }

}