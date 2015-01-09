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

package com.github.parboiled1.grappa.matchers;

import com.github.parboiled1.grappa.matchers.base.AbstractMatcher;
import com.github.parboiled1.grappa.matchers.base.Matcher;
import org.parboiled.MatcherContext;
import org.parboiled.support.Chars;

import static org.parboiled.support.Chars.escape;

/**
 * A {@link Matcher} matching a single given character.
 */
public final class CharMatcher
    extends AbstractMatcher
{
    private final char character;

    public CharMatcher(final char character)
    {
        super(getLabel(character));
        this.character = character;
    }

    public char getCharacter()
    {
        return character;
    }

    // TODO: remove...
    private static String getLabel(final char c)
    {
        switch (c) {
            case Chars.DEL_ERROR:
            case Chars.INS_ERROR:
            case Chars.RESYNC:
            case Chars.RESYNC_START:
            case Chars.RESYNC_END:
            case Chars.RESYNC_EOI:
            case Chars.EOI:
                return escape(c);
            default:
                return '\'' + escape(c) + '\'';
        }
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        if (context.getCurrentChar() != character)
            return false;
        context.advanceIndex(1);
        context.createNode();
        return true;
    }
}