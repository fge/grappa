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

import com.github.parboiled1.grappa.matchers.join.JoinMatcher;
import com.google.common.base.Preconditions;
import com.github.parboiled1.grappa.matchers.AbstractMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import com.github.parboiled1.grappa.matchers.Matcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.TestMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * A MatcherVisitor that executes a given {@link Action} against a whole matcher
 * hierarchy in a depth-first manner. Potential cycles are detected and not
 * rerun.
 */
public final class DoWithMatcherVisitor
    extends DefaultMatcherVisitor<Void>
{
    // TODO rename; unfortunately mixable with org.parboiled.Action<V>
    public interface Action
    {
        void process(Matcher matcher);
    }

    private final Action action;
    private final Set<Matcher> visited = new HashSet<>();

    public DoWithMatcherVisitor(final Action action)
    {
        this.action = Preconditions.checkNotNull(action, "action");
    }

    @Override
    public Void visit(final FirstOfMatcher matcher)
    {
        if (visited.add(matcher)) {
            for (final Matcher m : matcher.getChildren())
                m.accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    public Void visit(final JoinMatcher matcher)
    {
        if (visited.add(matcher)) {
            matcher.getJoined().accept(this);
            matcher.getJoining().accept(this);
            action.process(matcher);
        }
        return null;
    }


    @Override
    public Void visit(final SequenceMatcher matcher)
    {
        if (visited.add(matcher)) {
            for (final Matcher m : matcher.getChildren())
                m.accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    public Void visit(final OneOrMoreMatcher matcher)
    {
        if (visited.add(matcher)) {
            matcher.getSubMatcher().accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    public Void visit(final OptionalMatcher matcher)
    {
        if (visited.add(matcher)) {
            matcher.getSubMatcher().accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    public Void visit(final TestMatcher matcher)
    {
        if (visited.add(matcher)) {
            matcher.getSubMatcher().accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    public Void visit(final TestNotMatcher matcher)
    {
        if (visited.add(matcher)) {
            matcher.getSubMatcher().accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    public Void visit(final ZeroOrMoreMatcher matcher)
    {
        if (visited.add(matcher)) {
            matcher.getSubMatcher().accept(this);
            action.process(matcher);
        }
        return null;
    }

    @Override
    protected Void defaultValue(final AbstractMatcher matcher)
    {
        if (visited.add(matcher))
            action.process(matcher);
        return null;
    }
}
