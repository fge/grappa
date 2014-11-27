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

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.matchervisitors.MatcherVisitor;

/**
 * A {@link Matcher} trying all of its submatchers in sequence and succeeding when the first submatcher succeeds.
 */
@WillBeFinal(version = "1.1")
public class FirstOfMatcher
    extends CustomDefaultLabelMatcher<FirstOfMatcher>
{
    public FirstOfMatcher(final Rule[] subRules)
    {
        super(Preconditions.checkNotNull(subRules, "subRules"), "firstOf");
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        for (final Matcher matcher: getChildren()) {
            if (!matcher.getSubContext(context).runMatcher())
                continue;
            context.createNode();
            return true;
        }
        return false;
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}