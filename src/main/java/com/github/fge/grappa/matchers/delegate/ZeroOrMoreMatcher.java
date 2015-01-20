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

import com.github.fge.grappa.matchers.base.CustomDefaultLabelMatcher;
import com.github.fge.grappa.matchers.base.Matcher;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.errors.GrammarException;

/**
 * A {@link Matcher} that repeatedly tries its submatcher against the input. Always succeeds.
 */
public final class ZeroOrMoreMatcher
    extends CustomDefaultLabelMatcher<ZeroOrMoreMatcher>
{
    private final Matcher subMatcher;

    public ZeroOrMoreMatcher(final Rule subRule)
    {
        super(Preconditions.checkNotNull(subRule, "subRule"), "zeroOrMore");
        subMatcher = getChildren().get(0);
    }

    public Matcher getSubMatcher()
    {
        return subMatcher;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        Preconditions.checkNotNull(context, "context");
        int lastIndex = context.getCurrentIndex();
        while (subMatcher.getSubContext(context).runMatcher()) {
            final int currentLocation = context.getCurrentIndex();
            if (currentLocation == lastIndex) {
                throw new GrammarException(
                    "The inner rule of zeroOrMore rule '%s' must not allow empty matches",
                    context.getPath());
            }
            lastIndex = currentLocation;
        }

        context.createNode();
        return true;
    }
}
