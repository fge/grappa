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
import org.parboiled.matchers.unicode.UnicodeCharMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;
import org.parboiled.matchers.unicode.UnicodeRangeMatcher;
import org.parboiled.support.Chars;

/**
 * A {@link MatcherVisitor} determining whether a matcher can start a match with a given char.
 */
public class IsStarterCharVisitor implements MatcherVisitor<Boolean> {

    private final CanMatchEmptyVisitor canMatchEmptyVisitor = new CanMatchEmptyVisitor();
    private final char starterChar;

    public IsStarterCharVisitor(char starterChar) {
        this.starterChar = starterChar;
    }

    @Override
    public Boolean visit(ActionMatcher matcher) {
        return false;
    }

    @Override
    public Boolean visit(AnyMatcher matcher) {
        return starterChar != Chars.EOI;
    }

    @Override
    public Boolean visit(CharIgnoreCaseMatcher matcher) {
        return matcher.charLow == starterChar || matcher.charUp == starterChar;
    }

    @Override
    public Boolean visit(CharMatcher matcher) {
        return matcher.character == starterChar;
    }

    @Override
    public Boolean visit(final UnicodeCharMatcher matcher)
    {
        return matcher.isStarterChar(starterChar);
    }

    @Override
    public Boolean visit(CharRangeMatcher matcher) {
        return matcher.cLow <= starterChar && starterChar <= matcher.cHigh;
    }

    @Override
    public Boolean visit(final UnicodeRangeMatcher matcher)
    {
        return matcher.isStarterChar(starterChar);
    }

    @Override
    public Boolean visit(AnyOfMatcher matcher) {
        return matcher.characters.contains(starterChar);
    }

    @Override
    public Boolean visit(CustomMatcher matcher) {
        return matcher.isStarterChar(starterChar);
    }

    @Override
    public Boolean visit(EmptyMatcher matcher) {
        return false;
    }

    @Override
    public Boolean visit(FirstOfMatcher matcher) {
        for (Matcher child : matcher.getChildren()) {
            if (child.accept(this)) return true;
        }
        return false;
    }

    @Override
    public Boolean visit(NothingMatcher matcher) {
        return false;
    }

    @Override
    public Boolean visit(OneOrMoreMatcher matcher) {
        return matcher.subMatcher.accept(this);
    }

    @Override
    public Boolean visit(OptionalMatcher matcher) {
        return matcher.subMatcher.accept(this);
    }

    @Override
    public Boolean visit(SequenceMatcher matcher) {
        for (Matcher child : matcher.getChildren()) {
            if (child.accept(this)) return true;
            if (!child.accept(canMatchEmptyVisitor)) break;
        }
        return false;
    }

    @Override
    public Boolean visit(TestMatcher matcher) {
        return matcher.subMatcher.accept(this);
    }

    @Override
    public Boolean visit(TestNotMatcher matcher) {
        return false;
    }

    @Override
    public Boolean visit(ZeroOrMoreMatcher matcher) {
        return matcher.subMatcher.accept(this);
    }

}
