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

        int cycles = 1;
        int beforeCycle = context.getCurrentIndex();

        while (cycles < maxCycles && matchCycle(context, beforeCycle)) {
            beforeCycle = context.getCurrentIndex();
            cycles++;
        }

        context.setCurrentIndex(beforeCycle);

        if (cycles < minCycles)
            return false;

        context.createNode();
        return true;
    }
}
