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

package com.github.parboiled1.grappa.matchers.delegate;

import com.github.parboiled1.grappa.matchers.base.CustomDefaultLabelMatcher;
import com.github.parboiled1.grappa.matchers.base.Matcher;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/**
 * A {@link Matcher} that tries its submatcher once against the input and always succeeds.
 */
public final class OptionalMatcher
    extends CustomDefaultLabelMatcher<OptionalMatcher>
{
    private final Matcher subMatcher;

    public OptionalMatcher(final Rule subRule)
    {
        super(Preconditions.checkNotNull(subRule, "subRule"), "optional");
        subMatcher = getChildren().get(0);
    }

    public Matcher getSubMatcher()
    {
        return subMatcher;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        subMatcher.getSubContext(context).runMatcher();
        context.createNode();
        return true;
    }
}
