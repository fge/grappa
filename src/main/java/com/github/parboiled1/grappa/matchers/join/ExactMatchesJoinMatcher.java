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

        int cycles = 1;
        int beforeCycle = context.getCurrentIndex();

        while (cycles < nrCycles && matchCycle(context, beforeCycle)) {
            beforeCycle = context.getCurrentIndex();
            cycles++;
        }

        context.setCurrentIndex(beforeCycle);

        if (cycles != nrCycles)
            return false;

        context.createNode();
        return true;
    }
}
