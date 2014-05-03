package org.parboiled.matchers.join;

import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.errors.GrammarException;
import org.parboiled.matchers.CustomDefaultLabelMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchervisitors.MatcherVisitor;

public abstract class JoinMatcher
    extends CustomDefaultLabelMatcher<JoinMatcher>
{
    private static final int JOINED_CHILD_INDEX = 0;
    private static final int JOINING_CHILD_INDEX = 1;

    protected final Matcher joined;
    protected final Matcher joining;

    protected JoinMatcher(final Rule joined, final Rule joining)
    {
        super(new Rule[] { joined, joining }, "Join");
        this.joined = getChildren().get(JOINED_CHILD_INDEX);
        this.joining = getChildren().get(JOINING_CHILD_INDEX);
    }

    public final Matcher getJoined()
    {
        return joined;
    }

    public final Matcher getJoining()
    {
        return joining;
    }

    /**
     * Accepts the given matcher visitor.
     *
     * @param visitor the visitor
     * @return the value returned by the given visitor
     */
    @Override
    public final <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor);
        return visitor.visit(this);
    }

    protected final <V> boolean firstCycle(final MatcherContext<V> context,
        final int beforeCycle)
    {
        if (!joining.getSubContext(context).runMatcher())
            return false;
        if (context.getCurrentIndex() == beforeCycle)
            throw new GrammarException("joining rule (%s) of a JoinMatcher" +
                " cannot match an empty character sequence!", joining);
        return joined.getSubContext(context).runMatcher();
    }
}
