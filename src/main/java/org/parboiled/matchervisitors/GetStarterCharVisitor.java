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

import org.parboiled.matchers.AnyMatcher;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.CharIgnoreCaseMatcher;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.CustomMatcher;
import org.parboiled.matchers.unicode.UnicodeCharMatcher;
import org.parboiled.matchers.unicode.UnicodeRangeMatcher;
import org.parboiled.support.Characters;

import java.util.Random;

/**
 * Returns the first character a given matcher can start a match with.
 * For all complex matchers, i.e. the ones not always matching just one character, the visitor returns null.
 */
public final class GetStarterCharVisitor
    extends DefaultMatcherVisitor<Character>
{
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @Override
    public Character visit(final AnyMatcher matcher)
    {
        return 'X';
    }

    @Override
    public Character visit(final AnyOfMatcher matcher)
    {
        final Characters characters = matcher.characters;
        if (!characters.isSubtractive())
            return characters.getChars()[0];

        // for substractive sets we try to randomly choose a fitting character
        char c;
        do {
            c = (char) RANDOM.nextInt(Character.MAX_VALUE);
        } while (!Character.isDefined(c) || !characters.contains(c));
        return c;
    }

    @Override
    public Character visit(final CharIgnoreCaseMatcher matcher)
    {
        return matcher.charLow;
    }

    @Override
    public Character visit(final CharMatcher matcher)
    {
        return matcher.character;
    }

    @Override
    public Character visit(final CharRangeMatcher matcher)
    {
        return matcher.cLow;
    }

    @Override
    public Character visit(final UnicodeCharMatcher matcher)
    {
        return matcher.getStarterChar();
    }

    @Override
    public Character visit(final UnicodeRangeMatcher matcher)
    {
        return matcher.getStarterChar();
    }

    @Override
    public Character visit(final CustomMatcher matcher)
    {
        return matcher.getStarterChar();
    }
}
