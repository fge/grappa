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

import org.parboiled.matchers.ActionMatcher;
import org.parboiled.matchers.AnyMatcher;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.CharIgnoreCaseMatcher;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.CustomMatcher;
import org.parboiled.matchers.EmptyMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.NothingMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.OptionalMatcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.matchers.TestMatcher;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;
import org.parboiled.matchers.join.JoinMatcher;
import org.parboiled.matchers.unicode.UnicodeCharMatcher;
import org.parboiled.matchers.unicode.UnicodeRangeMatcher;
import org.parboiled.support.Checks;

/**
 * A {@link MatcherVisitor} determining whether a matcher can legally succeed with an empty match.
 */
public final class CanMatchEmptyVisitor
    implements MatcherVisitor<Boolean>
{

    @Override
    public Boolean visit(final ActionMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final AnyMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final CharIgnoreCaseMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final CharMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final UnicodeCharMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final UnicodeRangeMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final CharRangeMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final AnyOfMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final CustomMatcher matcher)
    {
        return matcher.canMatchEmpty();
    }

    @Override
    public Boolean visit(final EmptyMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final FirstOfMatcher matcher)
    {
        for (final Matcher child : matcher.getChildren())
            if (child.accept(this))
                return true;
        return false;
    }

    @Override
    public Boolean visit(final NothingMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final JoinMatcher matcher)
    {
        final Matcher joiningRule = matcher.getJoining();
        Checks.ensure(!joiningRule.accept(this), "the joining rule of a " +
            "JoinMatcher cannot accept empty matches (culprit rule: %s)",
            joiningRule);
        return false;
    }

    // TODO: test
    @Override
    public Boolean visit(final OneOrMoreMatcher matcher)
    {
        final Matcher m = matcher.subMatcher;
        Checks.ensure(!m.accept(this), "A OneOrMore matcher cannot have an " +
            "embedded rule accepting empty matches (culprit rule: '%s')", m);
        return false;
    }

    @Override
    public Boolean visit(final OptionalMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final SequenceMatcher matcher)
    {
        for (final Matcher child : matcher.getChildren()) {
            if (!child.accept(this))
                return false;
        }
        return true;
    }

    @Override
    public Boolean visit(final TestMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final TestNotMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final ZeroOrMoreMatcher matcher)
    {
        final Matcher m = matcher.subMatcher;
        Checks.ensure(!m.accept(this), "A ZeroOrMore matcher cannot have an " +
            "embedded rule accepting empty matches (culprit rule: '%s')", m);
        return true;
    }

}