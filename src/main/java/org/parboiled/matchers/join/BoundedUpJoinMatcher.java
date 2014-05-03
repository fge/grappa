package org.parboiled.matchers.join;

import com.google.common.annotations.Beta;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A joining matcher with a maximum number of matches to perform
 */
@Beta
public final class BoundedUpJoinMatcher
    extends JoinMatcher
{
    private final int maxCycles;

    public BoundedUpJoinMatcher(final Rule joined, final Rule joining,
        final int maxCycles)
    {
        super(joined, joining);
        this.maxCycles = maxCycles;
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
        /*
         * TODO! Check logic
         *
         * Here we are bounded up ONLY, which means the lower bound is 0; as
         * such, if the joining rule can match empty and we do not trigger the
         * joining rule at all due to this step failing, the fact that it may
         * match empty will go UNNOTICED!
         */
        if (!joined.getSubContext(context).runMatcher()) {
            context.createNode();
            return true;
        }

        /*
         * TODO: fix that...
         *
         * As a virtue of the constructor (JoinMatcherBuilder), in this
         * particular implementation we know that we have to match 2 or more (if
         * 1, an OptionalMatcher(joinedRule) is returned). We have to try at
         * least one more cycle.
         *
         * Unfortunately, we have to "waste" that second cycle to check whether
         * the joining rule can match an empty sequence :/ This is the same
         * story with ZeroOrMoreMatcher and OneOrMoreMatcher; due to
         * ProxyMatcher, this cannot be done before this point. It can all be
         * solved if we use a builder system instead!
         */

        int beforeCycle;
        beforeCycle = context.getCurrentIndex();
        if (!firstCycle(context, beforeCycle)) {
            context.setCurrentIndex(beforeCycle);
            return true;
        }

        /*
         * We did; so, at least two cycles completed.
         */
        int cycles = 2;

        /*
         * We still try and match as much as possible however; so continue to
         * cycle through "joining, joined"; we must not, however, match _more_
         * than the maximum we are allocated.
         */
        while (cycles < maxCycles) {
            beforeCycle = context.getCurrentIndex();
            if (joining.getSubContext(context).runMatcher()
                && joined.getSubContext(context).runMatcher()) {
                cycles++;
                continue;
            }
            context.setCurrentIndex(beforeCycle);
            break;
        }

        context.createNode();
        return true;
    }
}
