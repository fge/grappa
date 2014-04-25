package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;

public class SingleLeadSurrogateRangeMatcher
    extends UnicodeRangeMatcher
{
    private final char lead;
    private final char lowTrail;
    private final char highTrail;

    SingleLeadSurrogateRangeMatcher(final String label, final char lead,
        final char lowTrail, final char highTrail)
    {
        super(label);
        this.lead = lead;
        this.lowTrail = lowTrail;
        this.highTrail = highTrail;
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
        return c == lead;
    }

    @Override
    public char getStarterChar()
    {
        return lead;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        char tested;

        // Test the lead surrogate...
        tested = context.getCurrentChar();
        if (tested != lead)
            return false;

        // OK, there may be a match; we need to test for the trailing surrogate.
        context.advanceIndex(1);
        tested = context.getCurrentChar();

        if (tested >= lowTrail && tested <= highTrail) {
            // Match!
            context.advanceIndex(1);
            context.createNode();
            return true;
        }

        // No match. Too bad.
        context.advanceIndex(-1);
        return false;
    }
}
