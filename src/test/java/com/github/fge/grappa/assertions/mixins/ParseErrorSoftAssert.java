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

package com.github.fge.grappa.assertions.mixins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fge.grappa.buffers.InputBuffer;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.errors.ParseError;

public final class ParseErrorSoftAssert
    implements ParseError
{
    @JsonIgnore
    private final SoftAssertions soft = new SoftAssertions();
    private final int index;

    @JsonCreator
    public ParseErrorSoftAssert(@JsonProperty("index") final int index)
    {
        this.index = index;
    }

    /**
     * Gets the inputbuffer this error occurred in.
     *
     * @return the inputbuffer
     */
    @Override
    @JsonIgnore
    public InputBuffer getInputBuffer()
    {
        return null;
    }

    /**
     * Gets the start index of the parse error in the underlying input buffer.
     *
     * @return the input index of the first character covered by this error
     */
    @Override
    @JsonIgnore
    public int getStartIndex()
    {
        return 0;
    }

    /**
     * Gets the end index of the parse error in the underlying input buffer.
     *
     * @return the end index of this error, i.e. the index of the character
     * immediately following the last character
     * covered by this error
     */
    @Override
    @JsonIgnore
    public int getEndIndex()
    {
        return 0;
    }

    /**
     * An optional error message.
     *
     * @return an optional error message.
     */
    @Override
    public String getErrorMessage()
    {
        return null;
    }

    public ParseErrorSoftAssert matchesError(final ParseError error)
    {
        soft.assertThat(error.getStartIndex()).as("error index matches")
            .isEqualTo(index);
        soft.assertAll();
        return this;
    }

}
