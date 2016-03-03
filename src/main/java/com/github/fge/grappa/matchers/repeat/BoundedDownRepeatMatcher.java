package com.github.fge.grappa.matchers.repeat;

import com.github.fge.grappa.rules.Rule;

public final class BoundedDownRepeatMatcher
    extends RepeatMatcher
{
    private final int minCycles;

    public BoundedDownRepeatMatcher(final Rule subRule, final int minCycles)
    {
        super(subRule);
        this.minCycles = minCycles;
    }

    @Override
    protected boolean enoughCycles(final int cycles)
    {
        return cycles >= minCycles;
    }

    @Override
    protected boolean runAgain(final int cycles)
    {
        return true;
    }
}
