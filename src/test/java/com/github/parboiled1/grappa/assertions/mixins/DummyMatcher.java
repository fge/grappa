/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.parboiled1.grappa.assertions.mixins;

import org.parboiled.MatcherContext;
import com.github.parboiled1.grappa.matchers.CustomMatcher;

final class DummyMatcher
    extends CustomMatcher
{
    DummyMatcher()
    {
        super("dummy");
    }

    /**
     * Determines whether this matcher instance always matches exactly one
     * character.
     *
     * @return true if this matcher always matches exactly one character
     */
    @Override
    public boolean isSingleCharMatcher()
    {
        return false;
    }

    /**
     * Determines whether this matcher instance allows empty matches.
     *
     * @return true if this matcher instance allows empty matches
     */
    @Override
    public boolean canMatchEmpty()
    {
        return false;
    }

    /**
     * Determines whether this matcher instance can start a match with the
     * given char.
     *
     * @param c the char
     * @return true if this matcher instance can start a match with the given
     * char.
     */
    @Override
    public boolean isStarterChar(final char c)
    {
        return false;
    }

    /**
     * Returns one of possibly several chars that a match can start with.
     *
     * @return a starter char
     */
    @Override
    public char getStarterChar()
    {
        return 0;
    }

    /**
     * Tries a match on the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if the match was successful
     */
    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return false;
    }
}
