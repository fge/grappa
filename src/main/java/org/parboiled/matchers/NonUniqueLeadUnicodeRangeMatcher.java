package org.parboiled.matchers;

import org.parboiled.MatcherContext;
import org.parboiled.buffers.InputBuffer;

public class NonUniqueLeadUnicodeRangeMatcher
    extends UnicodeRangeMatcher
{
    private final int low;
    private final int high;
    private final char lowLead;
    private final char highLead;

    NonUniqueLeadUnicodeRangeMatcher(final String label, final int low,
        final int high)
    {
        super(label);
        this.low = low;
        this.high = high;

        lowLead = Character.toChars(low)[0];
        highLead = Character.toChars(high)[0];
    }

    @Override
    public boolean isStarterChar(final char c)
    {
        return c >= lowLead && c <= highLead;
    }

    @Override
    public char getStarterChar()
    {
        // Don't bother... Return the lower bound lead surrogate
        return lowLead;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final int index = context.getCurrentIndex();
        final InputBuffer buffer = context.getInputBuffer();

        /*
         * Extract the next two characters from the input, then ask Character
         * to make a code point out of it for us
         */
        final String extract = buffer.extract(index, index + 2);
        final int inputChar = Character.codePointAt(extract, 0);

        if (inputChar < low || inputChar > high)
            return false;

        // Match... Advance
        context.advanceIndex(2);
        context.createNode();
        return true;
    }
}
