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

package org.parboiled.parserunners;

import com.google.common.base.Preconditions;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.support.ParsingResult;

/**
 * A {@link ParseRunner} implementation that properly reports the first {@link InvalidInputError} if the input
 * does not conform to the rule grammar.
 * It performs exactly as the {@link BasicParseRunner} on valid input, however, on invalid input two more parsing
 * runs are initiated: one for recording the first parse error and one for collecting the error report information.
 */
public class ReportingParseRunner<V>
    extends AbstractParseRunner<V>
{

    /**
     * Creates a new ReportingParseRunner instance for the given rule.
     *
     * @param rule the parser rule
     */
    public ReportingParseRunner(final Rule rule)
    {
        super(rule);
    }

    @Override
    public ParsingResult<V> run(final InputBuffer inputBuffer)
    {
        Preconditions.checkNotNull(inputBuffer, "inputBuffer");
        resetValueStack();

        // first, run a basic match
        ParsingResult<V> result = runBasicMatch(inputBuffer);
        if (result.isSuccess())
            return result; // all good

        // ok, we have a parse error, so determine the error location
        resetValueStack();
        result = runLocatingMatch(inputBuffer);
        // we failed before so we should really be failing again
        Preconditions.checkState(!result.isSuccess());
        // may be more than one in case of custom ActionExceptions
        Preconditions.checkState(result.hasCollectedParseErrors());

        // finally perform a third, reporting run (now that we know the error
        // location)
        resetValueStack();
        result = runReportingMatch(inputBuffer,
            result.getParseErrors().get(0).getStartIndex());
        // we failed before so we should really be failing again
        Preconditions.checkState(!result.isSuccess());
        return result;
    }

    protected ParsingResult<V> runBasicMatch(final InputBuffer inputBuffer)
    {
        final ParseRunner<V> basicRunner
            = new BasicParseRunner<V>(rootMatcher)
            .withParseErrors(parseErrors)
            .withValueStack(valueStack);
        return basicRunner.run(inputBuffer);
    }

    protected ParsingResult<V> runLocatingMatch(final InputBuffer inputBuffer)
    {
        final ParseRunner<V> locatingRunner
            = new ErrorLocatingParseRunner<V>(rootMatcher)
            .withValueStack(valueStack);
        return locatingRunner.run(inputBuffer);
    }

    protected ParsingResult<V> runReportingMatch(final InputBuffer inputBuffer,
        final int errorIndex)
    {
        final ParseRunner<V> reportingRunner
            = new ErrorReportingParseRunner<V>(rootMatcher, errorIndex)
            .withParseErrors(parseErrors)
            .withValueStack(valueStack);
        return reportingRunner.run(inputBuffer);
    }
}

