package org.parboiled.matchers.join;

import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A joining matcher with a minimum number of matches to perform
 */
public final class BoundedDownJoinMatcher
    extends JoinMatcher
{
    private final int minMatches;

    public BoundedDownJoinMatcher(final Rule joined, final Rule joining,
        final int minMatches)
    {
        super(joined, joining);
        this.minMatches = minMatches;
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
