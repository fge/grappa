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

package com.github.fge.grappa.matchers.unicode;

import com.github.fge.grappa.matchers.MatcherType;
import org.parboiled.MatcherContext;

/**
 * A specialized version of a supplementary range matcher
 *
 * <p>This matcher is used when the range of characters asked for obey both of
 * the following rules:</p>
 *
 * <ul>
 *     <li>they are in the supplementary plane;</li>
 *     <li>both the lower bound and the upper bound share the same lead
 *     surrogate.</li>
 * </ul>
 *
 */
public final class SingleLeadSurrogateRangeMatcher
    extends UnicodeRangeMatcher
{
    private final char lead;
    private final char lowTrail;
    private final char highTrail;

    SingleLeadSurrogateRangeMatcher(final String label, final char lead,
        final char lowTrail, final char highTrail)
    {
        super(label);
        this.lead = lead;
        this.lowTrail = lowTrail;
        this.highTrail = highTrail;
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.TERMINAL;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        char tested;

        // Test the lead surrogate...
        tested = context.getCurrentChar();
        if (tested != lead)
            return false;

        // OK, there may be a match; we need to test for the trailing surrogate.
        context.advanceIndex(1);
        tested = context.getCurrentChar();

        if (tested >= lowTrail && tested <= highTrail) {
            // Match!
            context.advanceIndex(1);
            context.createNode();
            return true;
        }

        // No match. Too bad.
        context.advanceIndex(-1);
        return false;
    }
}
