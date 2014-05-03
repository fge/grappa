package org.parboiled.matchers.join;

import com.google.common.annotations.Beta;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/*
 * A joining matcher with a minimum and maximum number of matches to perform
 */
@Beta
public final class BoundedBothJoinMatcher
    extends JoinMatcher
{
    private final int minCycles;
    private final int maxCycles;

    public BoundedBothJoinMatcher(final Rule joined, final Rule joining,
        final int minCycles, final int maxCycles)
    {
        super(joined, joining);
        this.minCycles = minCycles;
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
         * We know that minCycles cannot be 0; if we don't match the first
         * joined, this is a failure.
         */
        if  (!joined.getSubContext(context).runMatcher())
            return false;

        /*
         * First cycle...
         */
        int beforeCycle;

        beforeCycle = context.getCurrentIndex();

        if (!firstCycle(context, beforeCycle)) {
            context.setCurrentIndex(beforeCycle);
            if (minCycles != 1)
                return false;
        }

        /*
         * We have completed at least two cycles
         */

        int nrCycles = 2;

        /*
         * Try and go up to the maximum number of cycles
         */
        while (nrCycles < maxCycles) {
            beforeCycle = context.getCurrentIndex();
            if (joining.getSubContext(context).runMatcher()
                && joined.getSubContext(context).runMatcher()) {
                nrCycles++;
                continue;
            }
            context.setCurrentIndex(beforeCycle);
            break;
        }

        /*
         * Success if and only if the number of cycles completed is greater than
         * or equal to the minimum required number of cycles
         */
        if (nrCycles < minCycles)
            return false;

        context.createNode();
        return true;
    }
}
