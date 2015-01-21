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

package org.parboiled.buffers;

import static org.parboiled.support.Chars.DEDENT;
import static org.parboiled.support.Chars.EOI;
import static org.parboiled.support.Chars.INDENT;

/**
 * UNUSED!
 *
 * @deprecated will be removed in 1.1
 */
// TODO: move to test
@Deprecated
public final class InputBufferUtils
{
    private InputBufferUtils()
    {
    }

    /**
     * Collects the actual input text the input buffer provides into a String.
     * This is especially useful for IndentDedentInputBuffers created by
     * "transformIndents".
     *
     * @param buf the input buffer to collect from
     * @return a string containing the content of the given input buffer
     */
    public static String collectContent(final InputBuffer buf)
    {
        final StringBuilder sb = new StringBuilder();

        int ix = 0;
        char c;
loop:
        while (true) {
            c = buf.charAt(ix++);
            switch (c) {
                case INDENT:
                    sb.append('\u00bb'); // right pointed double angle quotation mark
                    break;
                case DEDENT:
                    sb.append('\u00ab'); // left pointed double angle quotation mark
                    break;
                case EOI:
                    break loop;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}