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
import com.github.parboiled1.grappa.matchers.trie.TrieMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeCharMatcher;
import com.github.parboiled1.grappa.matchers.unicode.UnicodeRangeMatcher;
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
import org.parboiled.support.Chars;

/**
 * A {@link MatcherVisitor} determining whether a matcher can start a match with a given char.
 */
public final class IsStarterCharVisitor
    implements MatcherVisitor<Boolean>
{
    private final CanMatchEmptyVisitor canMatchEmptyVisitor
        = new CanMatchEmptyVisitor();
    private final char starterChar;

    public IsStarterCharVisitor(final char starterChar)
    {
        this.starterChar = starterChar;
    }

    @Override
    public Boolean visit(final ActionMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final AnyMatcher matcher)
    {
        return starterChar != Chars.EOI;
    }

    @Override
    public Boolean visit(final CharIgnoreCaseMatcher matcher)
    {
        return matcher.getLowerBound() == starterChar
            || matcher.getUpperBound() == starterChar;
    }

    @Override
    public Boolean visit(final CharMatcher matcher)
    {
        return matcher.getCharacter() == starterChar;
    }

    @Override
    public Boolean visit(final UnicodeCharMatcher matcher)
    {
        return matcher.isStarterChar(starterChar);
    }

    @SuppressWarnings("ImplicitNumericConversion")
    @Override
    public Boolean visit(final CharRangeMatcher matcher)
    {
        return matcher.getLowerBound() <= starterChar
            && starterChar <= matcher.getUpperBound();
    }

    @Override
    public Boolean visit(final UnicodeRangeMatcher matcher)
    {
        return matcher.isStarterChar(starterChar);
    }

    @Override
    public Boolean visit(final AnyOfMatcher matcher)
    {
        return matcher.getCharacters().contains(starterChar);
    }

    @Override
    public Boolean visit(final TrieMatcher matcher)
    {
        return matcher.trieHasStart(starterChar);
    }

    @Override
    public Boolean visit(final CustomMatcher matcher)
    {
        return matcher.isStarterChar(starterChar);
    }

    @Override
    public Boolean visit(final EmptyMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final FirstOfMatcher matcher)
    {
        for (final Matcher child: matcher.getChildren())
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
        return matcher.getJoined().accept(this);
    }

    @Override
    public Boolean visit(final OneOrMoreMatcher matcher)
    {
        return matcher.getSubMatcher().accept(this);
    }

    @Override
    public Boolean visit(final OptionalMatcher matcher)
    {
        return matcher.getSubMatcher().accept(this);
    }

    @Override
    public Boolean visit(final SequenceMatcher matcher)
    {
        for (final Matcher child : matcher.getChildren()) {
            if (child.accept(this))
                return true;
            if (!child.accept(canMatchEmptyVisitor))
                break;
        }
        return false;
    }

    @Override
    public Boolean visit(final TestMatcher matcher)
    {
        return matcher.getSubMatcher().accept(this);
    }

    @Override
    public Boolean visit(final TestNotMatcher matcher)
    {
        return false;
    }

    @Override
    public Boolean visit(final ZeroOrMoreMatcher matcher)
    {
        return matcher.getSubMatcher().accept(this);
    }
}
