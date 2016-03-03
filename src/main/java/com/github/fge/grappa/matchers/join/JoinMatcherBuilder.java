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

package com.github.fge.grappa.matchers.join;

import com.github.fge.grappa.matchers.EmptyMatcher;
import com.github.fge.grappa.matchers.delegate.OptionalMatcher;
import com.github.fge.grappa.misc.RangeMatcherBuilder;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.google.common.annotations.Beta;
import com.google.common.collect.Range;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The final step to building a {@link JoinMatcher}
 *
 * <p>At this step of the build, we have both rules (the "joined" rule and the
 * "joining" rule). The final information to feed to the matcher is the number
 * of cycles.</p>
 *
 * <p>The number of cycles can be bounded on the lower end and on the upper end.
 * The "true" building method is {@link #range(Range)}; all other methods
 * ultimately call this one to generate the result.</p>
 *
 * <p>The real matcher generated depends on the number of cycles required (for
 * the notation used here, see the javadoc for {@link Range}):</p>
 *
 * <ul>
 *     <li>[0..0]: an {@link EmptyMatcher};</li>
 *     <li>[0..1]: an {@link OptionalMatcher} with the joined rule as a
 *     submatcher;</li>
 *     <li>[1..1]: the "joined" rule itself;</li>
 *     <li>[n..+∞) for whatever n: a {@link BoundedDownJoinMatcher};</li>
 *     <li>[0..n] for n &gt;= 2: a {@link BoundedUpJoinMatcher};</li>
 *     <li>[n..n] for n &gt;= 2: an {@link ExactMatchesJoinMatcher};</li>
 *     <li>[n..m]: a {@link BoundedBothJoinMatcher}.</li>
 * </ul>
 *
 * @see JoinMatcher
 * @see Range
 */
@ParametersAreNonnullByDefault
@Beta
public final class JoinMatcherBuilder<V>
    extends RangeMatcherBuilder<V>
{
    private static final Range<Integer> AT_LEAST_ZERO = Range.atLeast(0);

    private final Rule joined;

    JoinMatcherBuilder(final Rule joined, final BaseParser<V> parser,
        final Rule joining)
    {
        super(parser, joining);
        this.joined = joined;
    }

    @Override
    protected Rule boundedDown(final int minCycles)
    {
        return new BoundedDownJoinMatcher(joined, rule, minCycles);
    }

    @Override
    protected Rule boundedUp(final int maxCycles)
    {
        return new BoundedUpJoinMatcher(joined, rule, maxCycles);
    }

    @Override
    protected Rule exactly(final int nrCycles)
    {
        return new ExactMatchesJoinMatcher(joined, rule, nrCycles);
    }

    @Override
    protected Rule boundedBoth(final int minCycles, final int maxCycles)
    {
        return new BoundedBothJoinMatcher(joined, rule, minCycles, maxCycles);
    }
}
