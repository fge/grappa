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

package org.parboiled.matchers;

import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.github.parboiled1.grappa.cleanup.WillBePrivate;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.matchervisitors.MatcherVisitor;

/**
 * A {@link Matcher} that tries its submatcher once against the input and always succeeds.
 */
@WillBeFinal(version = "1.1")
public class OptionalMatcher
    extends CustomDefaultLabelMatcher<OptionalMatcher>
{
    @WillBePrivate(version = "1.1")
    public final Matcher subMatcher;

    public OptionalMatcher(final Rule subRule)
    {
        super(Preconditions.checkNotNull(subRule, "subRule"), "Optional");
        subMatcher = getChildren().get(0);
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        subMatcher.getSubContext(context).runMatcher();
        context.createNode();
        return true;
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}
