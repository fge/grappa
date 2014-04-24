package org.parboiled.matchers.unicode;

import org.parboiled.common.Preconditions;
import org.parboiled.matchervisitors.MatcherVisitor;

public abstract class UnicodeCharMatcher
    extends UnicodeMatcher
{
    public static UnicodeCharMatcher forCodePoint(final int codePoint)
    {
        final String label = String.format("U+%04X", codePoint);
        final char[] chars = Character.toChars(codePoint);
        return chars.length == 1
            ? new BMPUnicodeCharMatcher(label, chars[0])
            : new SupplementaryUnicodeCharMatcher(label, chars);
    }

    protected UnicodeCharMatcher(final String label)
    {
        super(label);
    }

    @Override
    public final <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkArgNotNull(visitor, "visitor");
        return visitor.visit(this);
    }

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
