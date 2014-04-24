package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharRangeMatcher;

public class BmpRangeMatcher
    extends UnicodeRangeMatcher
{
    private final CharRangeMatcher matcher;

    BmpRangeMatcher(final String label, final char low, final char high)
    {
        super(label);
        matcher = new CharRangeMatcher(low, high);
    }

    @Override
    public boolean matchesSingleCharOnly()
    {
        return true;
    }

    @Override
    public boolean canStartWithChar(final char c)
    {
        return c >= matcher.cLow && c <= matcher.cHigh;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return matcher.match(context);
    }

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
