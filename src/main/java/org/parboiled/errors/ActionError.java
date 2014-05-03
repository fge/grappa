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

import com.google.common.base.Preconditions;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.support.MatcherPath;

/**
 * A {@link ParseError} wrapping an ActionException.
 */
public class ActionError
    extends BasicParseError
{

    private final MatcherPath errorPath;
    private final ActionException actionException;

    public ActionError(final InputBuffer inputBuffer, final int errorIndex,
        final String errorMessage, final MatcherPath errorPath,
        final ActionException actionException)
    {
        super(Preconditions.checkNotNull(inputBuffer), errorIndex,
            errorMessage);
        this.errorPath = Preconditions.checkNotNull(errorPath);
        this.actionException = Preconditions.checkNotNull(actionException);
    }

    /**
     * Gets the path to the matcher that caused this error.
     *
     * @return the MatcherPath
     */
    public MatcherPath getErrorPath()
    {
        return errorPath;
    }

    /**
     * Gets the wrapped ActionException.
     *
     * @return the wrapped ActionException
     */
    public ActionException getActionException()
    {
        return actionException;
    }
}
