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

import com.google.common.base.Preconditions;
import org.parboiled.common.IntArrayStack;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.support.Chars;
import org.parboiled.support.IndexRange;
import org.parboiled.support.Position;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

/**
 * Immutable default implementation of an InputBuffer.
 */
@Immutable
public final class DefaultInputBuffer
    implements InputBuffer
{
    private final int length;
    private final char[] buffer;

    // the indices of the newline characters in the buffer
    // built lazily, since the newline information is normally only needed in the case of parse errors when
    // error messages need to be generated
    private int[] newlines;

    /**
     * Constructs a new DefaultInputBuffer wrapping the given char array.
     * CAUTION: For performance reasons the given char array is not defensively copied.
     *
     * @param buffer the chars
     */
    public DefaultInputBuffer(final char[] buffer)
    {
        this.buffer = Preconditions.checkNotNull(buffer);
        length = buffer.length;
    }

    @Override
    public char charAt(final int index)
    {
        return 0 <= index && index < length ? buffer[index]
            : index - length > 100000 ? throwParsingException() : Chars.EOI;
    }

    private char throwParsingException()
    {
        throw new ParserRuntimeException(
            "Parser read more than 100K chars beyond EOI, "
                + "verify that your grammar does not consume EOI indefinitely!");
    }

    @Override
    public boolean test(final int index, final char[] characters)
    {
        final int len = characters.length;
        if (index < 0 || index > length - len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (buffer[index + i] != characters[i])
                return false;
        }
        return true;
    }

    @Override
    public String extract(int start, int end)
    {
        if (start < 0)
            start = 0;
        if (end >= length)
            end = length;
        if (end <= start)
            return "";
        return new String(buffer, start, end - start);
    }

    @Override
    public String extract(final IndexRange range)
    {
        return new String(buffer, range.start,
            Math.min(range.end, length) - range.start);
    }

    @Override
    public Position getPosition(final int index)
    {
        buildNewlines();
        final int line = getLine0(newlines, index);
        final int column = index - (line > 0 ? newlines[line - 1] : -1);
        return new Position(line + 1, column);
    }

    @Override
    public int getOriginalIndex(final int index)
    {
        return index;
    }

    // returns the zero based input line number the character with the given index is found in
    private static int getLine0(final int[] newlines, final int index)
    {
        final int j = Arrays.binarySearch(newlines, index);
        return j >= 0 ? j : -(j + 1);
    }

    @Override
    public String extractLine(final int lineNumber)
    {
        buildNewlines();
        Preconditions
            .checkArgument(0 < lineNumber && lineNumber <= newlines.length + 1);
        final int start = lineNumber > 1 ? newlines[lineNumber - 2] + 1 : 0;
        int end = lineNumber <= newlines.length ? newlines[lineNumber - 1]
            : length;
        if (charAt(end - 1) == '\r')
            end--;
        return extract(start, end);
    }

    @Override
    public int getLineCount()
    {
        buildNewlines();
        return newlines.length + 1;
    }

    private void buildNewlines()
    {
        if (newlines != null)
            return;
        final IntArrayStack stack = new IntArrayStack();
        for (int i = 0; i < length; i++)
            if (buffer[i] == '\n')
                stack.push(i);

        newlines = new int[stack.size()];
        stack.getElements(newlines, 0);
    }
}
