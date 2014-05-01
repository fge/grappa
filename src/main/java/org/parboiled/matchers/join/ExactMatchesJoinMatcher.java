package org.parboiled.matchers.join;

import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A matcher which must match exactly n times. Note that n is >= 2 (otherwise
 * the builder would have returned an empty matcher or the joined rule)
 */
public final class ExactMatchesJoinMatcher
    extends JoinMatcher
{
    private final int nrMatches;

    public ExactMatchesJoinMatcher(final Rule joined, final Rule joining,
        final int nrMatches)
    {
        super(joined, joining);
        this.nrMatches = nrMatches;
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
