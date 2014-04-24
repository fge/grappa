package org.parboiled.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchervisitors.MatcherVisitor;

public final class SingleLeadSurrogateRangeMatcher
    extends SupplementaryRangeMatcher
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
    public boolean matchesSingleCharOnly()
    {
        return false;
    }

    @Override
    public boolean canStartWithChar(final char c)
    {
        return c == lead;
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

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        return null;
    }
}
