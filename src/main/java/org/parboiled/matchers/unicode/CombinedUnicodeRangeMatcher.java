package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharRangeMatcher;

public class CombinedUnicodeRangeMatcher
    extends UnicodeRangeMatcher
{
    final UnicodeRangeMatcher supplementary;
    final CharRangeMatcher bmp;

    CombinedUnicodeRangeMatcher(final String label, final CharRangeMatcher bmp,
        final UnicodeRangeMatcher supplementary)
    {
        super(label);
        this.bmp = bmp;
        this.supplementary = supplementary;
    }

    @Override
    public boolean isSingleCharMatcher()
    {
        return false;
    }

    @Override
    public boolean canMatchEmpty()
    {
        return false;
    }

    @Override
    public boolean isStarterChar(final char c)
    {
        return c >= bmp.cLow && c <= bmp.cHigh
            || supplementary.isStarterChar(c);
    }

    @Override
    public char getStarterChar()
    {
        return (char) Math.min(bmp.cLow, supplementary.getStarterChar());
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        /*
         * Start with the supplementary matcher. Java does allow to create
         * String constants with only lead surrogates, for instance, so we
         * might as well match as much as possible.
         */
        return supplementary.match(context)
            || bmp.match(context);
    }
}
