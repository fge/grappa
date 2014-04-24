package org.parboiled.matchers.unicode;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.matchers.unicode.UnicodeCharMatcher;
import org.parboiled.util.StatsAssert;
import org.testng.annotations.Test;

public class UnicodeCharMatcherTest
{
    private static final int PILE_OF_POO = 0x1f4a9;

    static class TestGrammar
        extends BaseParser<Void>
    {
        public Rule rule(final int codePoint)
        {
            return UnicodeChar(codePoint);
        }
    }

    @Test
    public void statsShowCorrectMatcherClassesForNonBMPChar()
    {
        final TestGrammar testGrammar
            = Parboiled.createParser(TestGrammar.class);

        final Rule rule = testGrammar.rule(PILE_OF_POO);
        StatsAssert.assertStatsForRule(rule)
            .hasCounted(1, UnicodeCharMatcher.class)
            .hasCountedNothingElse();
    }

    @Test
    public void statsShowCorrectMatcherClassesForBMPChar()
    {
        final TestGrammar testGrammar
            = Parboiled.createParser(TestGrammar.class);

        StatsAssert.assertStatsForRule(testGrammar.rule('a'))
            .hasCounted(1, UnicodeCharMatcher.class)
            .hasCountedNothingElse();
    }
}