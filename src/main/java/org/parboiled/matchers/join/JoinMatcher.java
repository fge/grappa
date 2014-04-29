package org.parboiled.matchers.join;

import com.google.common.collect.Range;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.matchers.CustomDefaultLabelMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchervisitors.MatcherVisitor;

public class JoinMatcher
    extends CustomDefaultLabelMatcher<JoinMatcher>
{
    private static final int JOINED_CHILD_INDEX = 0;
    private static final int JOINING_CHILD_INDEX = 1;
    private final Range<Integer> range;

    public JoinMatcher(final Rule joined, final Rule joining,
        final Range<Integer> range)
    {
        super(new Rule[] { joined, joining }, "Join");
        this.range = range;
    }

    // TODO: make final?
    public Matcher getJoiningRule()
    {
        return getChildren().get(1);
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

    /**
     * Accepts the given matcher visitor.
     *
     * @param visitor the visitor
     * @return the value returned by the given visitor
     */
    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        return null;
    }
}
