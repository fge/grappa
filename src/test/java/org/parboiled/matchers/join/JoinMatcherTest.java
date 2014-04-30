package org.parboiled.matchers.join;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.matchers.EmptyMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.OptionalMatcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public final class JoinMatcherTest<V>
{
    private BaseParser<V> parser;
    private Matcher joined;
    private Matcher joining;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void initRules()
    {
        parser = (BaseParser<V>) mock(BaseParser.class);
        when(parser.toRule(any()))
            .thenAnswer(new Answer<Object>()
            {
                @Override
                public Object answer(final InvocationOnMock invocation)
                {
                    return invocation.getArguments()[0];
                }
            });
        joined = mock(Matcher.class);
        joining = mock(Matcher.class);
    }

    @Test
    public void minMustBePositive()
    {
        final String expected = "illegal repetition number specified (-1)," +
            " must be 0 or greater";
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).min(-1);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @Test
    public void maxMustBePositive()
    {
        final String expected = "illegal repetition number specified (-1)," +
            " must be 0 or greater";
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).max(-1);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @Test
    public void timesMustBePositive()
    {
        final String expected = "illegal repetition number specified (-1)," +
            " must be 0 or greater";
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).times(-1);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @Test
    public void times2MinimumMustBePositive()
    {
        final String expected = "illegal repetition number specified (-1)," +
            " must be 0 or greater";
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).times(-1, 0);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @Test
    public void times2MaximumMustBeGreaterThanMinimum()
    {
        final String expected = "illegal range specified (3, 1): " +
            "maximum must be greater than minimum";
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).times(3, 1);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @Test
    public void rangeMustNotBeNull()
    {
        final String expected = "range must not be null";
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).range(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @Test
    public void rangeMustNotBeEmptyAfterIntersection()
    {
        final Range<Integer> range = Range.lessThan(0);
        final String expected = "illegal range " + range
            + ": should not be empty after intersection with "
            + Range.atLeast(0);
        try {
            new JoinedRuleBuilder<V, BaseParser<V>>(parser, joined)
                .using(joining).range(range);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            final String actual = e.getMessage();
            assertThat(actual).overridingErrorMessage(
                "Unexpected exception message!\nExpected: %s\nActual  : %s\n",
                expected, actual
            ).isEqualTo(expected);
        }
    }

    @DataProvider
    public Iterator<Object[]> getRanges()
    {
        final List<Object[]> list = Lists.newArrayList();

        list.add(new Object[] { Range.singleton(2), Range.singleton(2) });

        return list.iterator();
    }

    @Test
    public void maxOfZeroReturnsEmptyMatcher()
    {
        final Rule rule = new JoinedRuleBuilder<V, BaseParser<V>>(parser,
            joined).using(joining).max(0);
        final Class<?> actual = rule.getClass();

        assertThat(rule).overridingErrorMessage(
            "Wrong class! Expected %s, got %s",
            EmptyMatcher.class.getCanonicalName(), actual.getCanonicalName()
        ).isExactlyInstanceOf(EmptyMatcher.class);
    }

    @Test
    public void maxOfOneReturnsOptionalMatcher()
    {
        final Rule rule = new JoinedRuleBuilder<V, BaseParser<V>>(parser,
            joined).using(joining).max(1);
        final Class<?> actual = rule.getClass();

        assertThat(rule).overridingErrorMessage(
            "Wrong class! Expected %s, got %s",
            OptionalMatcher.class.getCanonicalName(), actual.getCanonicalName()
        ).isExactlyInstanceOf(OptionalMatcher.class);
    }
}