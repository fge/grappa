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

/**
 * Generic {@link UnicodeRangeMatcher} for a code point range outside the BMP
 *
 * <p>This matcher is used as a code point range matcher if the lower bound's
 * lead surrogate and the upper bound's lead surrogate are different (otherwise
 * a {@link SingleLeadSurrogateRangeMatcher} is used).</p>
 *
 * @see SupplementaryCharMatcher
 */
public class GenericSupplementaryRangeMatcher
    extends UnicodeRangeMatcher
{
    private final char[] lowChars;
    private final char[] highChars;
    private final int low;
    private final int high;

    GenericSupplementaryRangeMatcher(final String label, final char[] lowChars,
        final char[] highChars)
    {
        super(label);
        this.lowChars = lowChars;
        this.highChars = highChars;
        low = Character.toCodePoint(lowChars[0], lowChars[1]);
        high = Character.toCodePoint(highChars[0], highChars[1]);
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
        return c >= lowChars[0] && c <= highChars[0];
    }

    @Override
    public char getStarterChar()
    {
        return lowChars[0];
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final int index = context.getCurrentIndex();
        final String tmp = context.getInputBuffer().extract(index, index + 2);

        /*
         * This method will return a supplementary code point iif the two next
         * chars are a lead and trail surrogate
         */
        final int tested = Character.codePointAt(tmp, 0);
        if (tested < low || tested > high)
            return false;

        context.advanceIndex(2);
        context.createNode();
        return true;
    }
}
