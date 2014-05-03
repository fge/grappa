package org.parboiled.matchers.join;

import com.google.common.annotations.Beta;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A matcher which must match exactly n times. Note that n is >= 2 (otherwise
 * the builder would have returned an empty matcher or the joined rule)
 */
@Beta
public final class ExactMatchesJoinMatcher
    extends JoinMatcher
{
    private final int nrCycles;

    public ExactMatchesJoinMatcher(final Rule joined, final Rule joining,
        final int nrCycles)
    {
        super(joined, joining);
        this.nrCycles = nrCycles;
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
         * We know here that at least two cycles are required; JoinedRuleBuilder
         * will have returned an EmptyMatcher for "exactly 0 cycles" and the
         * joined rule itself for "exactly one cycle".
         */
        if (!joined.getSubContext(context).runMatcher())
            return false;

        /*
         * TODO: fix that...
         *
         * Unfortunately, we have to "waste" that cycle each time so as to
         * detect whether the joining rule matches empty :/
         *
         * This is the same story with ZeroOrMoreMatcher and OneOrMoreMatcher;
         * unfortunately, due to ProxyMatcher, this cannot be done before this
         * point. It can all be solved if we use a builder system instead!
         */

        int beforeCycle;
        beforeCycle = context.getCurrentIndex();
        if (!firstCycle(context, beforeCycle)) {
            context.setCurrentIndex(beforeCycle);
            return false;
        }

        /*
         * OK, we have at least two cycles completed.
         */
        int cycles = 2;

        /*
         * We go on until we have reached the number of required cycles, or
         * until we fail...
         */
        while (cycles < nrCycles) {
            beforeCycle = context.getCurrentIndex();
            if (joining.getSubContext(context).runMatcher()
                && joined.getSubContext(context).runMatcher()) {
                cycles++;
                continue;
            }
            /*
             * We fail if we reach this point; since we entered the loop it
             * means we didn't reach the number of required cycles.
             */
            context.setCurrentIndex(beforeCycle);
            return false;
        }

        context.createNode();
        return true;
    }
}
