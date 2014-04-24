package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharRangeMatcher;

public class CombinedUnicodeRangeMatcher
    extends UnicodeRangeMatcher
{
    final UnicodeRangeMatcher supplementaryMatcher;
    final CharRangeMatcher bmpMatcher;

    CombinedUnicodeRangeMatcher(final String label,
        final CharRangeMatcher bmpMatcher,
        final UnicodeRangeMatcher supplementaryMatcher)
    {
        super(label);
        this.bmpMatcher = bmpMatcher;
        this.supplementaryMatcher = supplementaryMatcher;
    }

    @Override
    public boolean matchesSingleCharOnly()
    {
        return false;
    }

    @Override
    public boolean canStartWithChar(final char c)
    {
        return c >= bmpMatcher.cLow && c <= bmpMatcher.cHigh
            || supplementaryMatcher.canStartWithChar(c);
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        /*
         * Start with the supplementary matcher. Java does allow to create
         * String constants with only lead surrogates, for instance, so we
         * might as well match as much as possible.
         */
        return supplementaryMatcher.match(context)
            || bmpMatcher.match(context);
    }

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
