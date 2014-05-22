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

        int cycles = 1;
        int beforeCycle = context.getCurrentIndex();

        while (cycles < maxCycles && matchCycle(context, beforeCycle)) {
            beforeCycle = context.getCurrentIndex();
            cycles++;
        }

        context.setCurrentIndex(beforeCycle);

        context.createNode();
        return true;
    }
}
