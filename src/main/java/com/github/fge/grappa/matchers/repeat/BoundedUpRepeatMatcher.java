package com.github.fge.grappa.matchers.repeat;

import com.github.fge.grappa.rules.Rule;

/**
 * A repeat matcher matching a given maximum number of times
 *
 * <p>Note that this means that it can match zero times, that is no input text.
 * </p>
 */
public final class BoundedUpRepeatMatcher
    extends RepeatMatcher
{
    private final int maxCycles;

    public BoundedUpRepeatMatcher(final Rule subRule, final int maxCycles)
    {
        super(subRule);
        this.maxCycles = maxCycles;
    }

    @Override
    protected boolean enoughCycles(final int cycles)
    {
        return true;
    }

    @Override
    protected boolean runAgain(final int cycles)
    {
        return cycles < maxCycles;
    }
}
