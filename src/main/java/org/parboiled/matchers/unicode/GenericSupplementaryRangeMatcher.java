package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchervisitors.MatcherVisitor;

public class GenericSupplementaryRangeMatcher
    extends SupplementaryRangeMatcher
{
    private final char[] lowChars;
    private final char[] highChars;
    private final int low;
    private final int high;

    GenericSupplementaryRangeMatcher(final String label, final char[] lowChars,
        final char[] highChars)
    {
        super(label);
        this.lowChars = lowChars;
        this.highChars = highChars;
        low = Character.toCodePoint(lowChars[0], lowChars[1]);
        high = Character.toCodePoint(highChars[0], highChars[1]);
    }

    @Override
    public boolean matchesSingleCharOnly()
    {
        return false;
    }

    @Override
    public boolean canStartWithChar(final char c)
    {
        return c >= lowChars[0] && c <= highChars[0];
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final int index = context.getCurrentIndex();
        final String tmp = context.getInputBuffer().extract(index, index + 2);

        /*
         * This method will return a supplementary code point iif the two next
         * chars are a lead and trail surrogate
         */
        final int tested = Character.codePointAt(tmp, 0);
        if (tested < low || tested > high)
            return false;

        context.advanceIndex(2);
        context.createNode();
        return true;
    }
}
