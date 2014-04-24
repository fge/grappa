package org.parboiled.matchers;

public abstract class UnicodeRangeMatcher
    extends CustomMatcher
{
    public static Matcher forRange(final int low, final int high)
    {
        final String label = String.format("U+%X-U+%X", low, high);

        final char[] lowChars = Character.toChars(low);
        final char[] highChars = Character.toChars(high);

        return lowChars[0] != highChars[0] // Differing lead surrogates?
            ? new NonUniqueLeadUnicodeRangeMatcher(label, low, high)
            : new UniqueLeadUnicodeRangeMatcher(label, lowChars[0],
                lowChars[1], highChars[1]);
    }



    protected UnicodeRangeMatcher(final String label)
    {
        super(label);
    }

    @Override
    public final boolean isSingleCharMatcher()
    {
        return false;
    }

    @Override
    public final boolean canMatchEmpty()
    {
        return false;
    }
}

