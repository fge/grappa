package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.buffers.InputBuffer;

public class SupplementaryUnicodeCharMatcher
    extends UnicodeCharMatcher
{
    private final char[] chars;

    SupplementaryUnicodeCharMatcher(final String label, final char[] chars)
    {
        super(label);
        this.chars = chars;
    }

    @Override
    public boolean matchesSingleCharOnly()
    {
        return false;
    }

    @Override
    public boolean canStartWithChar(final char c)
    {
        return c == chars[0];
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

    @Override
    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
