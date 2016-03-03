package com.github.fge.grappa.matchers.repeat;

import com.github.fge.grappa.rules.Rule;

public final class BoundedBothRepeatMatcher
    extends RepeatMatcher
{
    private final int minCycles;
    private final int maxCycles;

    public BoundedBothRepeatMatcher(final Rule subRule, final int minCycles,
        final int maxCycles)
    {
        super(subRule);
        this.minCycles = minCycles;
        this.maxCycles = maxCycles;
    }

    @Override
    protected boolean enoughCycles(final int cycles)
    {
        return cycles >= minCycles;
    }

    @Override
    protected boolean runAgain(final int cycles)
    {
        return cycles < maxCycles;
    }
}
