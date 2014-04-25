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
    public boolean isSingleCharMatcher()
    {
        return true;
    }

    @Override
    public boolean canMatchEmpty()
    {
        return false;
    }

    @Override
    public boolean isStarterChar(final char c)
    {
        return c >= matcher.cLow && c <= matcher.cHigh;
    }

    @Override
    public char getStarterChar()
    {
        return matcher.cLow;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return matcher.match(context);
    }
}
