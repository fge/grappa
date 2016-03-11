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

package com.github.fge.grappa.matchers.predicates;

import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.matchers.base.CustomDefaultLabelMatcher;
import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.context.MatcherContext;

import java.util.Objects;

/**
 * A "negative lookahead" matcher
 *
 * <p>This is the matcher used by {@link BaseParser#testNot(Object) testNot()}.
 * This matcher will run its submatcher and declare success if the submatcher
 * <em>fails</em>.</p>
 *
 * <p>However, unlike other delegating matchers, after the submatcher is done,
 * it will reset both the current index and value stack to the values they had
 * when entering this matcher. Among other things, this means that this matcher
 * never advances in the input text.</p>
 *
 * @see MatcherType#PREDICATE
 */
public final class TestNotMatcher
    extends CustomDefaultLabelMatcher<TestNotMatcher>
{
    private final Matcher subMatcher;

    public TestNotMatcher(final Rule subRule)
    {
        super(Objects.requireNonNull(subRule, "subRule"), "testNot");
        subMatcher = getChildren().get(0);
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.PREDICATE;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final int lastIndex = context.getCurrentIndex();
        final Object valueStackSnapshot
            = context.getValueStack().takeSnapshot();

        if (subMatcher.getSubContext(context).runMatcher())
            return false;

        // reset location, Test matchers never advance
        context.setCurrentIndex(lastIndex);

        // erase all value stack changes the the submatcher could have made
        context.getValueStack().restoreSnapshot(valueStackSnapshot);
        return true;
    }
}
