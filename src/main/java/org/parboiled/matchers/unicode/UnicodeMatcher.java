package org.parboiled.matchers.unicode;

import org.parboiled.matchers.AbstractMatcher;

public abstract class UnicodeMatcher
    extends AbstractMatcher
{
    public UnicodeMatcher(final String label)
    {
        super(label);
    }

    public abstract boolean matchesSingleCharOnly();

    public abstract boolean canStartWithChar(final char c);

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
