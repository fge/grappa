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

import com.github.parboiled1.grappa.matchers.ActionMatcher;
import com.github.parboiled1.grappa.matchers.AnyMatcher;
import com.github.parboiled1.grappa.matchers.AnyOfMatcher;
import com.github.parboiled1.grappa.matchers.CharIgnoreCaseMatcher;
import com.github.parboiled1.grappa.matchers.CharMatcher;
import com.github.parboiled1.grappa.matchers.CharRangeMatcher;
import com.github.parboiled1.grappa.matchers.EmptyMatcher;
import com.github.parboiled1.grappa.matchers.FirstOfMatcher;
import com.github.parboiled1.grappa.matchers.NothingMatcher;
import com.github.parboiled1.grappa.matchers.base.CustomMatcher;
import com.github.parboiled1.grappa.matchers.delegate.OneOrMoreMatcher;
import com.github.parboiled1.grappa.matchers.delegate.OptionalMatcher;
import com.github.parboiled1.grappa.matchers.delegate.SequenceMatcher;
import com.github.parboiled1.grappa.matchers.delegate.ZeroOrMoreMatcher;
import com.github.parboiled1.grappa.matchers.join.JoinMatcher;
import com.github.parboiled1.grappa.matchers.predicates.TestMatcher;
import com.github.parboiled1.grappa.matchers.predicates.TestNotMatcher;
import com.github.parboiled1.grappa.matchers.trie.TrieMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeCharMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeRangeMatcher;

/**
 * A {@link MatcherVisitor} determining whether a matcher is a basic single
 * character matcher.
 */
public final class IsSingleCharMatcherVisitor
    implements MatcherVisitor<Boolean>
{
    @Override
    public Boolean visit(final ActionMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final AnyMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final CharIgnoreCaseMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final CharMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final UnicodeCharMatcher matcher)
    {
        return matcher.isSingleCharMatcher();
    }

    @Override
    public Boolean visit(final CharRangeMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final UnicodeRangeMatcher matcher)
    {
        return matcher.isSingleCharMatcher();
    }

    @Override
    public Boolean visit(final TrieMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final AnyOfMatcher matcher)
    {
        return true;
    }

    @Override
    public Boolean visit(final CustomMatcher matcher)
    {
        return matcher.isSingleCharMatcher();
    }

    @Override
    public Boolean visit(final EmptyMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final JoinMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final FirstOfMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final NothingMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final OneOrMoreMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final OptionalMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final SequenceMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final TestMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final TestNotMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final ZeroOrMoreMatcher matcher)
    {
        return false;
    }
}