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

package com.github.parboiled1.grappa.matchers.unicode;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharMatcher;

public class BmpCharMatcher
    extends UnicodeCharMatcher
{
    private final CharMatcher matcher;

    BmpCharMatcher(final String label, final char c)
    {
        super(label);
        matcher = new CharMatcher(c);
    }

    @Override
    public boolean isSingleCharMatcher()
    {
        return true;
    }

    @Override
    public boolean canMatchEmpty()
    {
        return false;
    }

    @Override
    public boolean isStarterChar(final char c)
    {
        return c == matcher.character;
    }

    @Override
    public char getStarterChar()
    {
        return matcher.character;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return matcher.match(context);
    }
}
