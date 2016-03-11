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
import com.github.fge.grappa.run.context.MatcherContext;

import java.util.Objects;

/**
 * A {@link Matcher} trying all of its submatchers in order and succeeding when
 * the first submatcher succeeds.
 *
 * <p>This is the matcher used by {@link BaseParser#firstOf(Object, Object,
 * Object...) firstOf()}.</p>
 *
 * <p>Note that it is actually the first matching submatcher which wins; others
 * will not be considered. Among other things, this means that if you have a
 * rule defined as:</p>
 *
 * <pre>
 *     firstOf(rule1(), rule2())
 * </pre>
 *
 * <p>if {@code rule1()} succeeds, then {@code rule2()} will not be attempted
 * even if it could potentially match more input text.</p>
 */
public final class FirstOfMatcher
    extends CustomDefaultLabelMatcher<FirstOfMatcher>
{
    public FirstOfMatcher(final Rule[] subRules)
    {
        super(Objects.requireNonNull(subRules, "subRules"), "firstOf");
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.COMPOSITE;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        for (final Matcher matcher: getChildren())
            if (matcher.getSubContext(context).runMatcher())
                return true;
        return false;
    }
}
