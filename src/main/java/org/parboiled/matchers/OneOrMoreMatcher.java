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

import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.errors.GrammarException;
import org.parboiled.matchervisitors.MatcherVisitor;

/**
 * A {@link Matcher} that repeatedly tries its submatcher against the input.
 * Succeeds if its submatcher succeeds at least once.
 */
public final class OneOrMoreMatcher
    extends CustomDefaultLabelMatcher<OneOrMoreMatcher>
{
    private final Matcher subMatcher;

    public OneOrMoreMatcher(final Rule subRule)
    {
        super(Preconditions.checkNotNull(subRule, "subRule"), "oneOrMore");
        subMatcher = getChildren().get(0);
    }

    public Matcher getSubMatcher()
    {
        return subMatcher;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final boolean matched = subMatcher.getSubContext(context).runMatcher();
        if (!matched)
            return false;

        // collect all further matches as well
        // TODO: "optimize" first cyle away; also relevant for ZeroOrMoreMatcher
        int beforeMatch = context.getCurrentIndex();
        int afterMatch;
        while (subMatcher.getSubContext(context).runMatcher()) {
            afterMatch = context.getCurrentIndex();
            if (afterMatch != beforeMatch) {
                beforeMatch = afterMatch;
                continue;
            }
            throw new GrammarException("The inner rule of OneOrMore rule"
                + " '%s' must not allow empty matches", context.getPath());
        }

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
