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

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.google.common.base.Preconditions;
import org.parboiled.support.IndexRange;
import org.parboiled.support.Position;

import java.util.Arrays;

/**
 * An InputBuffer wrapping another InputBuffer and providing for the ability to
 * insert (and undo) characters at certain index positions. Inserted chars do
 * not appear in extracted text and have the same positions as the original
 * chars at their indices. Note that this implementation is optimized for a
 * rather small number of insertions and will perform badly with a large number
 * of insertions.
 */
public final class MutableInputBuffer
    implements InputBuffer
{
    private final InputBuffer buffer;
    private int[] inserts = new int[0];
    private char[] chars = new char[0];

    public MutableInputBuffer(final InputBuffer buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public char charAt(final int index)
    {
        final int j = Arrays.binarySearch(inserts, index);
        return j >= 0 ? chars[j] : buffer.charAt(index + j + 1);
    }

    @Override
    public boolean test(final int index, final char[] characters)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Position getPosition(final int index)
    {
        return buffer.getPosition(map(index));
    }

    @Override
    public int getOriginalIndex(final int index)
    {
        return buffer.getOriginalIndex(map(index));
    }

    @Override
    public String extractLine(final int lineNumber)
    {
        return buffer.extractLine(lineNumber);
    }

    @Override
    public String extract(final int start, final int end)
    {
        return buffer.extract(map(start), map(end));
    }

    @Override
    public String extract(final IndexRange range)
    {
        return buffer.extract(map(range.start), map(range.end));
    }

    @Override
    public int getLineCount()
    {
        return buffer.getLineCount();
    }

    private int map(final int index)
    {
        int j = Arrays.binarySearch(inserts, index);
        if (j < 0)
            j = -(j + 1);
        return index - j;
    }

    public void insertChar(final int index, final char c)
    {
        int j = Arrays.binarySearch(inserts, index);
        if (j < 0)
            j = -(j + 1);

        final char[] newChars = new char[chars.length + 1];
        System.arraycopy(chars, 0, newChars, 0, j);
        newChars[j] = c;
        System.arraycopy(chars, j, newChars, j + 1, chars.length - j);
        chars = newChars;

        final int[] newInserts = new int[inserts.length + 1];
        System.arraycopy(inserts, 0, newInserts, 0, j);
        newInserts[j] = index;
        for (int i = j; i < inserts.length; i++) {
            newInserts[i + 1] = inserts[i] + 1;
        }
        inserts = newInserts;
    }

    public char undoCharInsertion(final int index)
    {
        final int j = Arrays.binarySearch(inserts, index);
        Preconditions.checkArgument(j >= 0,
            "Cannot undo a non-existing insertion");
        final char removedChar = chars[j];

        final char[] newChars = new char[chars.length - 1];
        System.arraycopy(chars, 0, newChars, 0, j);
        System.arraycopy(chars, j + 1, newChars, j, newChars.length - j);
        chars = newChars;

        final int[] newInserts = new int[inserts.length - 1];
        System.arraycopy(inserts, 0, newInserts, 0, j);
        for (int i = j + 1; i < inserts.length; i++) {
            newInserts[i - 1] = inserts[i] - 1;
        }
        inserts = newInserts;
        return removedChar;
    }

    public void replaceInsertedChar(final int index, final char c)
    {
        final int j = Arrays.binarySearch(inserts, index);
        Preconditions.checkArgument(j >= 0,
            "Can only replace chars that were previously inserted");
        chars[j] = c;
    }
}
