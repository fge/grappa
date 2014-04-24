package org.parboiled.matchers;

import org.parboiled.MatcherContext;

public class BMPUnicodeCharMatcher
    extends UnicodeCharMatcher
{
    private final CharMatcher matcher;

    BMPUnicodeCharMatcher(final String label, final char c)
    {
        super(label);
        matcher = new CharMatcher(c);
    }

    @Override
    public boolean matchesSingleCharOnly()
    {
        return true;
    }

    @Override
    public boolean canStartWithChar(final char c)
    {
        return c == matcher.character;
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
