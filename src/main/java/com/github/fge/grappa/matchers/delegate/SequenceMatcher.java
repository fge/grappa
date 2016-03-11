/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package com.github.fge.grappa.matchers.delegate;

import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.matchers.base.CustomDefaultLabelMatcher;
import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.stack.ValueStack;
import com.github.fge.grappa.run.context.MatcherContext;

import java.util.Objects;

/**
 * A {@link Matcher} that executes all of its submatchers in sequence and only
 * succeeds if all submatchers succeed.
 *
 * <p>This is the matcher used by {@link BaseParser#sequence(Object[])
 * sequence()}; but it is also used in rules accepting more than one rule as
 * an argument, for instance {@code join(...)}, {@code zeroOrMore(...)}, {@code
 * test(...)} etc.</p>
 */
public final class SequenceMatcher
    extends CustomDefaultLabelMatcher<SequenceMatcher>
{
    public SequenceMatcher(final Rule[] subRules)
    {
        super(Objects.requireNonNull(subRules, "subRules"), "sequence");
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.COMPOSITE;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final ValueStack<V> stack = context.getValueStack();
        final Object snapshot = stack.takeSnapshot();

        for (final Matcher matcher: getChildren()) {
            if (matcher.getSubContext(context).runMatcher())
                continue;

            stack.restoreSnapshot(snapshot);
            return false;
        }
        return true;
    }
}
