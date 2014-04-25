package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharMatcher;

public class BmpCharMatcher
    extends UnicodeCharMatcher
{
    private final CharMatcher matcher;

    BmpCharMatcher(final String label, final char c)
    {
        super(label);
        matcher = new CharMatcher(c);
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
        return c == matcher.character;
    }

    @Override
    public char getStarterChar()
    {
        return matcher.character;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return matcher.match(context);
    }
}
