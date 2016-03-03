package com.github.fge.grappa.misc;

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.google.common.collect.Range;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public final class RangeMatcherBuilderTest
{
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private Rule rule;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private BaseParser<Object> parser;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private TestBuilder builder;

    @SuppressWarnings({ "unchecked", "UnsecureRandomNumberGeneration" })
    @BeforeMethod
    public void initParser()
    {
        rule = mock(Rule.class);

        parser = mock(BaseParser.class);

        builder = spy(new TestBuilder(parser, rule));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void minDelegateTest()
    {
        final int cycles = nextInt();

        final ArgumentCaptor<Range> captor
            = ArgumentCaptor.forClass(Range.class);

        builder.min(cycles);

        verify(builder).range(captor.capture());

        final Range<Integer> range = captor.getValue();

        assertThat(range).isEqualTo(Range.atLeast(cycles));
    }

    @Test(dependsOnMethods = "minDelegateTest")
    public void minTest()
    {
        final int cycles = nextInt();

        builder.range(Range.atLeast(cycles));

        verify(builder).boundedDown(cycles);
        verifyZeroInteractions(parser);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void maxDelegateTest()
    {
        final int cycles = nextInt();

        final ArgumentCaptor<Range> captor
            = ArgumentCaptor.forClass(Range.class);

        builder.max(cycles);

        verify(builder).range(captor.capture());

        final Range<Integer> range = captor.getValue();

        assertThat(range).isEqualTo(Range.atMost(cycles));
    }

    @Test(dependsOnMethods = "maxDelegateTest")
    public void maxTest()
    {
        final int cycles = 3;

        builder.max(cycles);

        verify(builder).boundedUp(cycles);
        verifyZeroInteractions(parser);
    }

    @Test(dependsOnMethods = "maxDelegateTest")
    public void maxZeroTest()
    {
        builder.range(Range.atMost(0));

        verify(builder, never()).boundedUp(anyInt());
        verify(parser, only()).empty();
    }

    @Test(dependsOnMethods = "maxDelegateTest")
    public void maxOneTest()
    {
        builder.range(Range.atMost(1));

        verify(builder, never()).boundedUp(anyInt());
        verify(parser, only()).optional(same(rule));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void singleArgumentTimeDelegateTest()
    {
        final int cycles = nextInt();

        builder.times(cycles);

        final ArgumentCaptor<Range> captor
            = ArgumentCaptor.forClass(Range.class);

        verify(builder).range(captor.capture());

        final Range<Integer> range = captor.getValue();

        assertThat(range).isEqualTo(Range.singleton(cycles));
    }

    @Test(dependsOnMethods = "singleArgumentTimeDelegateTest")
    public void singleArgumentTimesTest()
    {
        final int cycles = 3;

        builder.times(cycles);

        verify(builder).exactly(cycles);
        verifyZeroInteractions(parser);
    }

    @Test(dependsOnMethods = "singleArgumentTimeDelegateTest")
    public void timesZeroTest()
    {
        builder.times(0);

        verify(builder, never()).exactly(anyInt());
        verify(parser, only()).empty();
    }

    @Test(dependsOnMethods = "singleArgumentTimeDelegateTest")
    public void timesOneTest()
    {
        final Rule actual = builder.times(1);

        verify(builder, never()).exactly(anyInt());
        verifyZeroInteractions(parser);
        assertThat(actual).isSameAs(rule);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void twoArgumentsTimesDelegateTest()
    {
        final int minCycles = nextInt() / 2;
        final int maxCycles = minCycles + 1;

        builder.times(minCycles, maxCycles);

        final ArgumentCaptor<Range> captor
            = ArgumentCaptor.forClass(Range.class);

        verify(builder).range(captor.capture());

        final Range<Integer> range = captor.getValue();

        assertThat(range).isEqualTo(Range.closed(minCycles, maxCycles));
    }

    @Test(dependsOnMethods = "twoArgumentsTimesDelegateTest")
    public void twoArgumentsTimesTest()
    {
        final int minCycles = 3;
        final int maxCycles = 5;

        builder.times(minCycles, maxCycles);

        verify(builder).boundedBoth(minCycles, maxCycles);
        verifyZeroInteractions(parser);
    }

    @Test(dependsOnMethods = "twoArgumentsTimesDelegateTest")
    public void twoArgumentsTimesZeroTest()
    {
        builder.times(0, 0);

        verify(builder, never()).boundedBoth(anyInt(), anyInt());
        verify(parser, only()).empty();
    }

    @Test(dependsOnMethods = "twoArgumentsTimesDelegateTest")
    public void twoArgumentsTimesZeroOneTest()
    {
        builder.times(0, 1);

        verify(builder, never()).boundedBoth(anyInt(), anyInt());
        verify(parser, only()).optional(same(rule));
    }

    @Test(dependsOnMethods = "twoArgumentsTimesDelegateTest")
    public void twoArgumentsTimesOneTest()
    {
        final Rule actual = builder.times(1, 1);

        verify(builder, never()).boundedBoth(anyInt(), anyInt());
        verifyZeroInteractions(parser);
        assertThat(actual).isSameAs(rule);
    }


    @SuppressWarnings("ClassWithOnlyPrivateConstructors")
    private static class TestBuilder
        extends RangeMatcherBuilder<Object>
    {
        private TestBuilder(final BaseParser<Object> parser, final Rule rule)
        {
            super(parser, rule);
        }

        @Override
        protected Rule boundedDown(final int minCycles)
        {
            return null;
        }

        @Override
        protected Rule boundedUp(final int maxCycles)
        {
            return null;
        }

        @Override
        protected Rule exactly(final int nrCycles)
        {
            return null;
        }

        @Override
        protected Rule boundedBoth(final int minCycles, final int maxCycles)
        {
            return null;
        }
    }

    private static int nextInt(final int minValue)
    {
        int ret;

        do {
            ret = RANDOM.nextInt();
        } while (ret < minValue);

        return ret;
    }

    private static int nextInt()
    {
        return nextInt(0);
    }
}
