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

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.google.common.base.Preconditions;
import org.parboiled.support.MatcherPath;

import java.util.List;

/**
 * A {@link ParseError} describing one or more input characters that are illegal with regard to the underlying
 * language grammar.
 */
public final class InvalidInputError
    extends BasicParseError
{
    private final List<MatcherPath> failedMatchers;

    public InvalidInputError(final InputBuffer inputBuffer,
        final int startIndex, final List<MatcherPath> failedMatchers,
        final String errorMessage)
    {
        super(Preconditions.checkNotNull(inputBuffer, "inputBuffer"),
            startIndex, errorMessage);
        this.failedMatchers = Preconditions.checkNotNull(failedMatchers);
    }

    /**
     * Gets the list of paths to the single character matchers that failed at the error location of this error.
     *
     * @return the list of paths to the single character matchers that failed at the error location of this error
     */
    public List<MatcherPath> getFailedMatchers()
    {
        return failedMatchers;
    }
}

