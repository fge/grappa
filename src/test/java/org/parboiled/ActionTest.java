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

import com.github.parboiled1.grappa.assertions.OldStatsAssert;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.Label;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ActionTest extends ParboiledTest<Integer>
{

    public static class Actions extends BaseActions<Integer> {

        public boolean addOne() {
            Integer i = getContext().getValueStack().pop();
            getContext().getValueStack().push(i + 1);
            return true;
        }
    }

    @BuildParseTree
    public static class Parser extends BaseParser<Integer> {

        final Actions actions = new Actions();

        public Rule A() {
            return sequence(
                    'a',
                    push(42),
                    B(18),
                    stringAction("lastText:" + match())
            );
        }

        public Rule B(int i) {
            int j = i + 1;
            return sequence(
                    'b',
                    push(timesTwo(i + j)),
                    C(),
                    push(pop()) // no effect
            );
        }

        public Rule C() {
            return sequence(
                    'c',
                    push(pop()), // no effect
                    new Action() {
                        public boolean run(Context context) {
                            return getContext() == context;
                        }
                    },
                    D(1)
            );
        }

        @Label("Last")
        public Rule D(int i) {
            return sequence(
                    'd', dup(),
                    push(i),
                    actions.addOne()
            );
        }

        public boolean stringAction(String string) {
            return "lastText:bcd".equals(string);
        }

        // ************* ACTIONS **************

        public int timesTwo(int i) {
            return i * 2;
        }

    }

    @Test
    public void test() {
        Parser parser = Parboiled.createParser(Parser.class);
        test(parser.A(), "abcd")
                .hasNoErrors()
                .hasParseTree("" +
                        "[A, {2}] 'abcd'\n" +
                        "  ['a'] 'a'\n" +
                        "  [B, {2}] 'bcd'\n" +
                        "    ['b', {42}] 'b'\n" +
                        "    [C, {2}] 'cd'\n" +
                        "      ['c', {74}] 'c'\n" +
                        "      [Last, {2}] 'd'\n" +
                        "        ['d', {74}] 'd'\n");

        OldStatsAssert.assertStatsForRule(parser.A())
            .hasCountedTotal(17).hasCountedActions(9)
            .hasCounted(4, CharMatcher.class)
            .hasCounted(4, SequenceMatcher.class)
            .hasCountedActionClasses(8)
            .hasCountedNothingElse();

        ParserStatistics stats = ParserStatistics.generateFor(parser.A());

        assertEquals(stats.printActionClassInstances(), "" +
                "Action classes and their instances for rule 'A':\n" +
                "    Action$0QAUd2XJhkFkwVyB : D_Action2\n" +
                "    Action$BYXjsBCgkaYmhXqh : D_Action3\n" +
                "    Action$M1hejMpBJ5SjCHvC : B_Action1\n" +
                "    Action$Qy8BzTl3RpzAzrXV : A_Action1\n" +
                "    Action$Syt5vvsOCzKOZ8Az : D_Action1\n" +
                "    Action$esdKf3Sj9cYL9I1s : B_Action2, C_Action1\n" +
                "    Action$xipkqpZJpsrvOANU : A_Action2\n" +
                "    and 1 anonymous instance(s)\n");
    }

}