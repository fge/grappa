package org.parboiled.matchers;

import org.parboiled.common.Preconditions;
import org.parboiled.matchervisitors.MatcherVisitor;

public abstract class UnicodeCharMatcher
    extends AbstractMatcher
{
    protected UnicodeCharMatcher(final String label)
    {
        super(label);
    }

    public abstract boolean matchesSingleCharOnly();

    public abstract boolean canStartWithChar(final char c);

    @Override
    public final <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkArgNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}
