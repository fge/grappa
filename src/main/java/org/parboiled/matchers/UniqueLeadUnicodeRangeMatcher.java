package org.parboiled.matchers;

import org.parboiled.MatcherContext;

public class UniqueLeadUnicodeRangeMatcher
    extends UnicodeRangeMatcher
{
    private final char lead;
    private final char trailLow;
    private final char trailHigh;

    UniqueLeadUnicodeRangeMatcher(final String label, final char lead,
        final char trailLow, final char trailHigh)
    {
        super(label);
        this.lead = lead;
        this.trailLow = trailLow;
        this.trailHigh = trailHigh;
    }

    @Override
    public final boolean isStarterChar(final char c)
    {
        return c == lead;
    }

    @Override
    public final char getStarterChar()
    {
        return lead;
    }

    @Override
    public final <V> boolean match(final MatcherContext<V> context)
    {
        char c = context.getCurrentChar();
        // Start with the lead surrogate
        if (c != lead)
            return false;

        // OK, match, now try the trail one
        final int index = context.getCurrentIndex();
        context.advanceIndex(1);
        c = context.getCurrentChar();

        /*
         * OK, this is not really clean; c may be EOI here. However, a trail
         * surrogate can never be EOI, so this works...
         */
        if (c >= trailLow && c <= trailHigh) {
            // Full match
            context.advanceIndex(1);
            context.createNode();
            return true;
        }

        // No match: must backtrack
        context.setCurrentIndex(index);
        return false;
    }
}
