/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.parboiled1.grappa.matchers.join;

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

        if (!matchJoining(context, beforeCycle)) {
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
            if (matchJoining(context, beforeCycle)
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
