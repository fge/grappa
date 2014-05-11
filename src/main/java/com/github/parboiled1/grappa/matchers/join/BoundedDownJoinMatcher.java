package com.github.parboiled1.grappa.matchers.join;

import com.google.common.annotations.Beta;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A joining matcher with a minimum number of matches to perform
 */
@Beta
public final class BoundedDownJoinMatcher
    extends JoinMatcher
{
    private final int minCycles;

    public BoundedDownJoinMatcher(final Rule joined, final Rule joining,
        final int minCycles)
    {
        super(joined, joining);
        this.minCycles = minCycles;
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
         * We are bounded down here, and only down.
         *
         * However, the lower bound may be zero... So, if we don't have a match
         * here, too bad, the match succeeds...
         *
         * Too bad because it means we won't be able to check whether the
         * joining rule can match empty, which is illegal!
         */
        if (!joined.getSubContext(context).runMatcher()) {
            if (minCycles != 0)
                return false;
            context.createNode();
            return true;
        }

        /*
         * TODO: fix that...
         *
         * The "joining rule must not match empty" rule can only be checked
         * here. Unfortunately, it cannot be checked at parser class generation
         * time.
         *
         * Theoretically, we would have to "waste" the start of every second
         * cycle each time so as to detect whether the joining rule matches
         * empty... But we don't bother if only one cycle was required.
         */

        int beforeCycle;

        beforeCycle = context.getCurrentIndex();
        if (!firstCycle(context, beforeCycle)) {
            context.setCurrentIndex(beforeCycle);
            if (minCycles != 1)
                return false;
            context.createNode();
            return true;
        }

        /*
         * From this point on we have at least two matches.
         */
        int cycles = 2;

        /*
         * Keep on matching joining/joined until we cannot...
         */
        while (true) {
            beforeCycle = context.getCurrentIndex();
            if (joining.getSubContext(context).runMatcher()
                && joined.getSubContext(context).runMatcher()) {
                cycles++;
                continue;
            }
            context.setCurrentIndex(beforeCycle);
            break;
        }

        /*
         * OK, we cannot anymore, so check how much we have matched so far; if
         * it is less than what is required, we fail. Otherwise, success.
         */
        if (cycles < minCycles)
            return false;

        context.createNode();
        return true;
    }
}
