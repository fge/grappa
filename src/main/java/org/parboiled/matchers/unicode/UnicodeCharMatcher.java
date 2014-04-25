package org.parboiled.matchers.unicode;

import org.parboiled.matchers.CustomMatcher;

public abstract class UnicodeCharMatcher
    extends CustomMatcher
{
    public static UnicodeCharMatcher forCodePoint(final int codePoint)
    {
        final String label = String.format("U+%04X", codePoint);
        final char[] chars = Character.toChars(codePoint);
        return chars.length == 1
            ? new BmpCharMatcher(label, chars[0])
            : new SupplementaryCharMatcher(label, chars);
    }

    protected UnicodeCharMatcher(final String label)
    {
        super(label);
    }
}
