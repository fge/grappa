package org.parboiled.matchers.join;

import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A joining matcher with a minimum and maximum number of matches to perform
 */
public final class BoundedBothJoinMatcher
    extends JoinMatcher
{
    private final int minMatches;
    private final int maxMatches;

    public BoundedBothJoinMatcher(final Rule joined, final Rule joining,
        final int minMatches, final int maxMatches)
    {
        super(joined, joining);
        this.minMatches = minMatches;
        this.maxMatches = maxMatches;
    }

    /**
     * Tries a match on the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if the match was successful
     */
    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return false;
    }
}
