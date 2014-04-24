package org.parboiled.matchers;

import org.parboiled.MatcherContext;
import org.parboiled.matchervisitors.MatcherVisitor;

import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * A Unicode character matcher
 *
 * <p>This matcher will attempt to match a Unicode character which is outside
 * the <a
 * href="http://en.wikipedia.org/wiki/Basic_Multilingual_Plane">Basic
 * Multilingual Plane</a>, that is Unicode characters ranging from U+10000 to
 * U+10FFFF.</p>
 *
 * @see Character#toChars(int)
 */
public class SupplementaryCharMatcher
    extends CustomMatcher
{
    private final char[] chars;

    public SupplementaryCharMatcher(final int codePoint)
    {
        super(getLabel(codePoint));
        chars = Character.toChars(codePoint);
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final int index = context.getCurrentIndex();
        if (!context.getInputBuffer().test(index, chars))
            return false;
        context.advanceIndex(2);
        context.createNode();
        return true;
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
        return c == chars[0];
    }

    @Override
    public char getStarterChar()
    {
        return chars[0];
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        checkArgNotNull(visitor, "visitor");
        return visitor.visit(this);
    }

    private static String getLabel(final int codePoint)
    {
        if (!Character.isValidCodePoint(codePoint))
            throw new IllegalArgumentException("invalid code point "
                + codePoint);
        return String.format("U+%X", codePoint);
    }

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
