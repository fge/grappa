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

import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.matchers.base.AbstractMatcher;

/**
 * Base implementation of a Unicode code point matcher
 *
 * <p>This method will instantiate one of its children according to the value
 * of the code point passed as an argument:</p>
 *
 * <ul>
 *     <li>if the code point falls into the Basic Multilingual Plane, it will
 *     generate a {@link BmpCharMatcher};</li>
 *     <li>otherwise it will build a {@link SupplementaryCharMatcher}.</li>
 * </ul>
 */
public abstract class UnicodeCharMatcher
    extends AbstractMatcher
{
    public static UnicodeCharMatcher forCodePoint(final int codePoint)
    {
        final String label = String.format("U+%04X", codePoint);
        final char[] chars = Character.toChars(codePoint);
        return chars.length == 1
            ? new BmpCharMatcher(label, chars[0])
            : new SupplementaryCharMatcher(label, chars);
    }

    protected UnicodeCharMatcher(final String label)
    {
        super(label);
    }

    @Override
    public final MatcherType getType()
    {
        return MatcherType.TERMINAL;
    }
}
