package com.github.fge.grappa.matchers.repeat;

import com.github.fge.grappa.rules.Rule;

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
