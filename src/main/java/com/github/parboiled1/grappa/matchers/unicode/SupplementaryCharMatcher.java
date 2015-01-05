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

import com.github.parboiled1.grappa.buffers.InputBuffer;
import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharMatcher;

/**
 * A {@link UnicodeCharMatcher} for a single, supplementary code point
 *
 * <p>Java's {@code char} is in fact a UTF-16 code unit; but since the birth of
 * Java, Unicode has evolved beyond the basic multilingual plane, which means
 * code points are defined outside the range U+0000-U+FFFF.</p>
 *
 * <p>For these, Java uses two {@code char}s: a lead surrogate and a trail
 * surrogate. This pair is the exact pair used by UTF-16, and is obtained
 * using {@link Character#toChars(int)}. And this is what this matcher uses.</p>
 *
 * <p>For code points inside the basic multilingual plane, a "plain" {@link
 * CharMatcher} is used instead.</p>
 *
 * <p>For more information, see <a href="http://en.wikipedia.org/wiki/UTF-16"
 * target="_blank">the Wikipedia article on UTF-16</a>.</p>
 */
public class SupplementaryCharMatcher
    extends UnicodeCharMatcher
{
    private final char[] chars;

    SupplementaryCharMatcher(final String label, final char[] chars)
    {
        super(label);
        this.chars = chars;
    }

    @Override
    public boolean isSingleCharMatcher()
    {
        return false;
    }

    @Override
    public boolean canMatchEmpty()
    {
        return false;
    }

    @Override
    public boolean isStarterChar(final char c)
    {
        return c == chars[0];
    }

    @Override
    public char getStarterChar()
    {
        return chars[0];
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final InputBuffer buffer = context.getInputBuffer();
        final boolean success = buffer.test(context.getCurrentIndex(), chars);
        if (success) {
            context.advanceIndex(2);
            context.createNode();
        }
        return success;
    }
}
