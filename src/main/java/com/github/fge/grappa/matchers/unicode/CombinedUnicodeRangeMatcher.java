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

package com.github.fge.grappa.matchers.unicode;

import com.github.fge.grappa.matchers.CharRangeMatcher;
import com.github.fge.grappa.matchers.MatcherType;
import org.parboiled.MatcherContext;

/**
 * A {@link UnicodeRangeMatcher} for "mixed" code point ranges
 *
 * <p>Such a range contains a character in the basic multilingual plane as its
 * lower bound and a character in the supplementary plane at its upper bound.
 * </p>
 *
 * <p>When matching, the supplementary plane is checked first (using a {@link
 * GenericSupplementaryRangeMatcher} or a {@link
 * SingleLeadSurrogateRangeMatcher} as appropriate), then the basic multilingual
 * plane (using a {@link CharRangeMatcher}).</p>
 *
 * <p>For instance, given the range U+1200-U+12000, range U+1000-U+12000 will
 * be checked first and only then range U+1200-U+FFFF. This is done this way
 * because Java allows to create a "string" with only a lead surrogate as a
 * single {@code char} (which is invalid Unicode).</p>
 */
@SuppressWarnings("ImplicitNumericConversion")
public final class CombinedUnicodeRangeMatcher
    extends UnicodeRangeMatcher
{
    final UnicodeRangeMatcher supplementary;
    final CharRangeMatcher bmp;

    CombinedUnicodeRangeMatcher(final String label, final CharRangeMatcher bmp,
        final UnicodeRangeMatcher supplementary)
    {
        super(label);
        this.bmp = bmp;
        this.supplementary = supplementary;
    }

    @Override
    public MatcherType getType()
    {
        return MatcherType.COMPOSITE;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        /*
         * Start with the supplementary matcher. Java does allow to create
         * String constants with only lead surrogates, for instance, so we
         * might as well match as much as possible.
         */
        return supplementary.match(context)
            || bmp.match(context);
    }
}
