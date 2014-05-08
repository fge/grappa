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
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.MemoMismatchesMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.parserunners.ProfilingParseRunner;
import org.testng.annotations.Test;

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
    }
}