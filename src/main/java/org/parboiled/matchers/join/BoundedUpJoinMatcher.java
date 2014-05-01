package org.parboiled.matchers.join;

import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A joining matcher with a maximum number of matches to perform
 */
public final class BoundedUpJoinMatcher
    extends JoinMatcher
{
    private final int maxMatches;

    public BoundedUpJoinMatcher(final Rule joined, final Rule joining,
        final int maxMatches)
    {
        super(joined, joining);
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
