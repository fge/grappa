package org.parboiled.matchers.unicode;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.util.StatsAssert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class UnicodeCharMatcherTest
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

    @DataProvider
    public Iterator<Object[]> getClassInfo()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        list.add(new Object[] { (int) 'b', BmpCharMatcher.class });
        list.add(new Object[] { 0x1f4e3, SupplementaryCharMatcher.class });

        return list.iterator();
    }

    @Test(dataProvider = "getClassInfo")
    public void generatedMatcherClassIsWhatIsExpected(final int codePoint,
        final Class<? extends UnicodeCharMatcher> expected)
    {
        final Class<? extends UnicodeCharMatcher> actual
            = UnicodeCharMatcher.forCodePoint(codePoint).getClass();

        assertThat(actual).overridingErrorMessage(
            "Classes differ! Expected %s, got %s", expected, actual
        ).isSameAs(expected);
    }
}