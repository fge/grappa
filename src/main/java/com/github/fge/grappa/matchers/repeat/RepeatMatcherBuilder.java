package com.github.fge.grappa.matchers.repeat;

import com.github.fge.grappa.misc.RangeMatcherBuilder;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;

/**
 * Builder for a repeat matcher
 *
 * @param <V> parameter type of the elements on the parser stack
 *
 * @see BaseParser#repeat(Object)
 * @see BaseParser#repeat(Object, Object, Object...)
 */
public final class RepeatMatcherBuilder<V>
    extends RangeMatcherBuilder<V>
{
    public RepeatMatcherBuilder(final BaseParser<V> parser, final Rule rule)
    {
        super(parser, rule);
    }

    @Override
    protected Rule boundedDown(final int minCycles)
    {
        return new BoundedDownRepeatMatcher(rule, minCycles);
    }

    @Override
    protected Rule boundedUp(final int maxCycles)
    {
        return new BoundedUpRepeatMatcher(rule, maxCycles);
    }

    @Override
    protected Rule exactly(final int nrCycles)
    {
        return new ExactMatchesRepeatMatcher(rule, nrCycles);
    }

    @Override
    protected Rule boundedBoth(final int minCycles, final int maxCycles)
    {
        return new BoundedBothRepeatMatcher(rule, minCycles, maxCycles);
    }
}
