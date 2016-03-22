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

package com.github.fge.grappa.support;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class Chars
{
    private static final Map<Character, String> ESCAPE_MAP
        = ImmutableMap.<Character, String>builder()
        .put('\r', "\\r")
        .put('\n', "\\n")
        .put('\t', "\\t")
        .put('\f', "\\f")
        .put('\uFFFF', "EOI")
        .build();

    private Chars()
    {
    }

    /**
     * Return a map of characters to escape and their replacements
     *
     * @return an escape map (immutable)
     *
     * @see CharsEscaper
     */
    public static Map<Character, String> escapeMap()
    {
        return ESCAPE_MAP;
    }

    public static String escape(final char c)
    {
        return Optional.fromNullable(ESCAPE_MAP.get(c)).or(String.valueOf(c));
    }
}
