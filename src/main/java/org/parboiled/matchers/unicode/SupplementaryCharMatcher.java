package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.buffers.InputBuffer;

public class SupplementaryCharMatcher
    extends UnicodeCharMatcher
{
    private final char[] chars;

    SupplementaryCharMatcher(final String label, final char[] chars)
    {
        super(label);
        this.chars = chars;
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
    public <V> boolean match(final MatcherContext<V> context)
    {
        final InputBuffer buffer = context.getInputBuffer();
        final boolean success = buffer.test(context.getCurrentIndex(), chars);
        if (success) {
            context.advanceIndex(2);
            context.createNode();
        }
        return success;
    }
}
