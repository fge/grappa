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

package org.parboiled.matchervisitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.parboiled.MatcherContext;
import org.parboiled.matchers.AbstractMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;

import java.util.List;

/**
 * Collects the matchers that can legally follow the given matcher according to
 * the grammar into a given list. The visitor returns true if the collected
 * matchers are all possible followers, and false if other matchers higher up
 * the rule stack can also follow.
 */
public final class FollowMatchersVisitor
    extends DefaultMatcherVisitor<Boolean>
{
    private final CanMatchEmptyVisitor canMatchEmptyVisitor
        = new CanMatchEmptyVisitor();
    private final ImmutableList.Builder<Matcher> builder
        = ImmutableList.builder();
    private MatcherContext<?> context;

    public List<Matcher> getFollowMatchers(
        final MatcherContext<?> currentContext)
    {
        context = currentContext.getParent();
        while (context != null) {
            if (context.getMatcher().accept(this))
                break;
            context = context.getParent();
        }
        return builder.build();
    }

    @Override
    public Boolean visit(final OneOrMoreMatcher matcher)
    {
        builder.add(matcher.getSubMatcher());
        return false;
    }

    /*
     * TODO: check if this is correct
     *
     * The SequenceMatcher here will collect all of its matchers but "break out"
     * as long as one cannot match empty.
     *
     * OK, fine, but a SequenceMatcher is limited, a JoinMatcher may not be. As
     * a result, a JoinMatcher will always return false here! Even if it is
     * bounded.
     */
    @Override
    public Boolean visit(final SequenceMatcher matcher)
    {
        final int startTag = context.getIntTag() + 1;
        final List<Matcher> children = matcher.getChildren();
        for (final Matcher child: Iterables.skip(children, startTag)) {
            builder.add(child);
            if (!child.accept(canMatchEmptyVisitor))
                return true;
        }
        return false;
    }

    @Override
    public Boolean visit(final ZeroOrMoreMatcher matcher)
    {
        builder.add(matcher.getSubMatcher());
        return false;
    }

    @Override
    protected Boolean defaultValue(final AbstractMatcher matcher)
    {
        return false;
    }
}
