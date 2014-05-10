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

package org.parboiled.support;

import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.parboiled.buffers.IndentDedentInputBuffer;

import java.util.Arrays;
import java.util.Map;

//TODO: make final
@WillBeFinal(version = "1.1")
public class Chars
{
    /**
     * Special non-character used during error recovery. Signals that an illegal input character was skipped at this
     * input location.
     */
    public static final char DEL_ERROR = '\uFDEA';

    /**
     * Special non-character used during error recovery. Signals that the character at the following input location
     * was expected but not present in the input buffer.
     */
    public static final char INS_ERROR = '\uFDEB';

    /**
     * Special non-character used during error recovery. Signals that a rule resynchronization has to be performed
     * at the current input location.
     */
    public static final char RESYNC = '\uFDEC';

    /**
     * Special non-character used during error recovery. Signals that all characters up to the RESYNC_END
     * character need to be skipped as part of a resynchronization.
     */
    public static final char RESYNC_START = '\uFDED';

    /**
     * Special non-character used during error recovery. Signals the end of a resynchronization block.
     */
    public static final char RESYNC_END = '\uFDEE';

    /**
     * Special non-character used during error recovery. Signals a resynchronization at EOI.
     */
    public static final char RESYNC_EOI = '\uFDEF';

    /**
     * The End-of-Input non-character.
     */
    public static final char EOI = '\uFFFF';

    /**
     * Special non-character used by the {@link IndentDedentInputBuffer}.
     */
    @Deprecated
    public static final char INDENT = '\uFDD0';

    /**
     * Special non-character used by the {@link IndentDedentInputBuffer}.
     */
    @Deprecated
    public static final char DEDENT = '\uFDD1';

    private static final Map<Character, String> ESCAPE_MAP
        = ImmutableMap.<Character, String>builder()
        .put('\r', "\\r")
        .put('\n', "\\n")
        .put('\t', "\\t")
        .put('\f', "\\f")
        .put(DEL_ERROR, "DEL_ERROR")
        .put(INS_ERROR, "INS_ERROR")
        .put(RESYNC, "RESYNC")
        .put(RESYNC_START, "RESYNC_START")
        .put(RESYNC_END, "RESYNC_END")
        .put(RESYNC_EOI, "RESYNC_EOI")
        .put(INDENT, "INDENT")
        .put(DEDENT, "DEDENT")
        .put(EOI, "EOI")
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

    public static String repeat(final char c, final int n)
    {
        final char[] array = new char[n];
        Arrays.fill(array, c);
        return new String(array);
    }
}
