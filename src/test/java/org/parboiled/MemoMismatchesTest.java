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

import org.parboiled.annotations.MemoMismatches;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.MemoMismatchesMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.parserunners.ProfilingParseRunner;
import com.github.parboiled1.grappa.assertions.OldStatsAssert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class MemoMismatchesTest {

    static class Parser extends BaseParser<Integer> {

        Rule Clause() {
            return sequence(firstOf(Zero(), One(), Two()), EOI);
        }

        Rule Zero() {
            return sequence(testNot(SevenOrNine()), ch('0'));
        }

        Rule One() {
            return sequence(testNot(SevenOrNine()), ch('1'));
        }

        Rule Two() {
            return sequence(testNot(SevenOrNine()), ch('2'));
        }

        Rule SevenOrNine() {
            return firstOf('7', '9');
        }

    }

    static class MemoParser extends Parser {
        @Override
        @MemoMismatches
        Rule SevenOrNine() {
            return super.SevenOrNine();
        }
    }

    @Test
    public void test1() {
        Parser parser = Parboiled.createParser(Parser.class);

        OldStatsAssert.assertStatsForRule(parser.Clause()).hasCountedTotal(13)
            .hasCounted(6, CharMatcher.class)
            .hasCounted(2, FirstOfMatcher.class)
            .hasCounted(4, SequenceMatcher.class)
            .hasCounted(1, TestNotMatcher.class)
            .hasCountedNothingElse();

        ProfilingParseRunner runner = new ProfilingParseRunner(parser.Clause());
        assertFalse(runner.run("2").hasErrors());
        assertEquals(runner.getReport().printBasics().replaceFirst("\\d\\.\\d\\d\\d s", "X.XXX s"), "" +
                "Runs                     :               1\n" +
                "Active rules             :              13\n" +
                "Total net rule time      :           X.XXX s\n" +
                "Total rule invocations   :              21\n" +
                "Total rule matches       :               8\n" +
                "Total rule mismatches    :              13\n" +
                "Total match share        :           38.10 %\n" +
                "Rule re-invocations      :               8\n" +
                "Rule re-matches          :               2\n" +
                "Rule re-mismatches       :               6\n" +
                "Rule re-invocation share :           38.10 %\n");
    }

    @Test
    public void test2() {
        MemoParser parser = Parboiled.createParser(MemoParser.class);

        OldStatsAssert.assertStatsForRule(parser.Clause()).hasCountedTotal(13)
            .hasCounted(6, CharMatcher.class)
            .hasCounted(2, FirstOfMatcher.class)
            .hasCounted(4, SequenceMatcher.class)
            .hasCounted(1, TestNotMatcher.class)
            .hasCounted(1, MemoMismatchesMatcher.class)
            .hasCountedNothingElse();

        ProfilingParseRunner runner = new ProfilingParseRunner(parser.Clause());
        assertFalse(runner.run("2").hasErrors());
        assertEquals(runner.getReport().printBasics().replaceFirst("\\d\\.\\d\\d\\d s", "X.XXX s"), "" +
                "Runs                     :               1\n" +
                "Active rules             :              13\n" +
                "Total net rule time      :           X.XXX s\n" +
                "Total rule invocations   :              17\n" +
                "Total rule matches       :               8\n" +
                "Total rule mismatches    :               9\n" +
                "Total match share        :           47.06 %\n" +
                "Rule re-invocations      :               4\n" +
                "Rule re-matches          :               2\n" +
                "Rule re-mismatches       :               2\n" +
                "Rule re-invocation share :           23.53 %\n");
    }
}