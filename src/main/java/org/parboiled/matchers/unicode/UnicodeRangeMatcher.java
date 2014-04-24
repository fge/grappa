package org.parboiled.matchers.unicode;

import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchervisitors.MatcherVisitor;

public abstract class UnicodeRangeMatcher
    extends UnicodeMatcher
{
    private static final char[] MIN_SUPPLEMENTARY = Character.toChars(0x10000);

    public static UnicodeRangeMatcher forRange(final int low, final int high)
    {
        final String label = String.format("U+%04X-U+%04X", low, high);

        final char[] lowChars = Character.toChars(low);
        final char[] highChars = Character.toChars(high);

        if (lowChars.length == 1) {
            if (highChars.length == 1)
                return new BmpRangeMatcher(label, lowChars[0], highChars[0]);
            /*
             * OK, highChars is a supplementary code point. We need two
             * matchers: one for low-0xffff and one for 0x10000-high
             */
            final CharRangeMatcher bmpMatcher
                = new CharRangeMatcher(lowChars[0], Character.MAX_VALUE);
            final UnicodeRangeMatcher supplementary
                = supplementaryOf(label, MIN_SUPPLEMENTARY, highChars);
        }

        return null;
    }

    protected UnicodeRangeMatcher(final String label)
    {
        super(label);
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        return visitor.visit(this);
    }

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    private static UnicodeRangeMatcher supplementaryOf(final String label,
        final char[] lowChars, final char[] highChars)
    {
        return lowChars[0] == highChars[0]
            ? new SingleLeadSurrogateRangeMatcher(label, lowChars[0],
                lowChars[1], highChars[1])
            : new GenericSupplementaryRangeMatcher(label, lowChars, highChars);
    }
}
