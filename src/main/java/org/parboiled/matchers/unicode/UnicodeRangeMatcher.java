package org.parboiled.matchers.unicode;

import org.parboiled.common.Preconditions;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.CustomMatcher;
import org.parboiled.matchervisitors.MatcherVisitor;

public abstract class UnicodeRangeMatcher
    extends CustomMatcher
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
            final CharRangeMatcher bmp
                = new CharRangeMatcher(lowChars[0], Character.MAX_VALUE);
            final UnicodeRangeMatcher supplementary
                = supplementaryRange(label, MIN_SUPPLEMENTARY, highChars);
            return new CombinedUnicodeRangeMatcher(label, bmp, supplementary);
        }

        /*
         * Both are supplementary, so...
         */
        return supplementaryRange(label, lowChars, highChars);
    }

    protected UnicodeRangeMatcher(final String label)
    {
        super(label);
    }

    private static UnicodeRangeMatcher supplementaryRange(final String label,
        final char[] lowChars, final char[] highChars)
    {
        return lowChars[0] == highChars[0]
            ? new SingleLeadSurrogateRangeMatcher(label, lowChars[0],
                lowChars[1], highChars[1])
            : new GenericSupplementaryRangeMatcher(label, lowChars, highChars);
    }

    public <R> R accept(MatcherVisitor<R> visitor)
    {
        Preconditions.checkArgNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}
