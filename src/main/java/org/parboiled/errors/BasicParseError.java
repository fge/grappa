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

package org.parboiled.errors;

import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.google.common.base.Preconditions;
import org.parboiled.buffers.InputBuffer;

/**
 * A basic {@link ParseError} implementation for a one-char parse error with an optional error message.
 */
@WillBeFinal(version = "1.1")
public class BasicParseError
    implements ParseError
{
    private final InputBuffer inputBuffer;
    private final int startIndex;
    private final String errorMessage;
    private int endIndex;
    private int indexDelta;

    public BasicParseError(final InputBuffer inputBuffer, final int errorIndex,
        final String errorMessage)
    {
        this.inputBuffer = Preconditions.checkNotNull(inputBuffer);
        startIndex = errorIndex;
        endIndex = errorIndex + 1;
        this.errorMessage = errorMessage;
    }

    @Override
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }

    @Override
    public int getStartIndex()
    {
        return startIndex + indexDelta;
    }

    @Override
    public int getEndIndex()
    {
        return endIndex + indexDelta;
    }

    public void setEndIndex(final int endIndex)
    {
        this.endIndex = endIndex - indexDelta;
    }

    @Override
    public String getErrorMessage()
    {
        return errorMessage;
    }

    public int getIndexDelta()
    {
        return indexDelta;
    }

    public void shiftIndexDeltaBy(final int delta)
    {
        indexDelta += delta;
    }
}
