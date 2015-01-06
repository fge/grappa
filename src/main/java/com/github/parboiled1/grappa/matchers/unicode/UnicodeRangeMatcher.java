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

import com.google.common.base.Preconditions;
import com.github.parboiled1.grappa.matchers.CharRangeMatcher;
import com.github.parboiled1.grappa.matchers.CustomMatcher;
import org.parboiled.matchervisitors.MatcherVisitor;

/**
 * Base implementation of a Unicode code point range matcher
 *
 * <p>This matcher is also the "factory" for all its implementation using the
 * {@link #forRange(int, int)} method; according to the lower and upper bounds
 * of the range, it will generate either of a {@link BmpRangeMatcher}, a {@link
 * SingleLeadSurrogateRangeMatcher} or a {@link
 * GenericSupplementaryRangeMatcher}.</p>
 */
public abstract class UnicodeRangeMatcher
    extends CustomMatcher
{
    private static final char[] MIN_SUPPLEMENTARY = Character.toChars(0x10000);

    public static UnicodeRangeMatcher forRange(final int low, final int high)
    {
        final String label = String.format("U+%04X-U+%04X", low, high);

        final char[] lowChars = Character.toChars(low);
        final char[] highChars = Character.toChars(high);

        if (lowChars.length == 1) {
            if (highChars.length == 1)
                return new BmpRangeMatcher(label, lowChars[0], highChars[0]);
            /*
             * OK, highChars is a supplementary code point. We need two
             * matchers: one for low-0xffff and one for 0x10000-high
             */
            final CharRangeMatcher bmp
                = new CharRangeMatcher(lowChars[0], Character.MAX_VALUE);
            final UnicodeRangeMatcher supplementary
                = supplementaryRange(label, MIN_SUPPLEMENTARY, highChars);
            return new CombinedUnicodeRangeMatcher(label, bmp, supplementary);
        }

        /*
         * Both are supplementary, so...
         */
        return supplementaryRange(label, lowChars, highChars);
    }

    protected UnicodeRangeMatcher(final String label)
    {
        super(label);
    }

    private static UnicodeRangeMatcher supplementaryRange(final String label,
        final char[] lowChars, final char[] highChars)
    {
        return lowChars[0] == highChars[0]
            ? new SingleLeadSurrogateRangeMatcher(label, lowChars[0],
                lowChars[1], highChars[1])
            : new GenericSupplementaryRangeMatcher(label, lowChars, highChars);
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}
