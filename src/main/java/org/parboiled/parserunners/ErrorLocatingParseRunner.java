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
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.BasicParseError;
import org.parboiled.matchers.TestNotMatcher;
import org.parboiled.support.ParsingResult;

import javax.annotation.Nullable;

/**
 * A {@link ParseRunner} implementation that creates a simple {@link BasicParseError} for the first error found in the
 * input and adds it to the list of ParseErrors.
 * It never causes the parser to perform more than one parsing run and is rarely used directly.
 * Instead its functionality is relied upon by the {@link ReportingParseRunner} and {@link RecoveringParseRunner} classes.
 */
public final class ErrorLocatingParseRunner<V>
    extends AbstractParseRunner<V>
    implements MatchHandler
{
    private final MatchHandler inner;
    private int errorIndex;

    /**
     * Creates a new ErrorLocatingParseRunner instance for the given rule.
     *
     * @param rule the parser rule
     */
    // TODO: replace null with an appropriate, "dummy" handler
    public ErrorLocatingParseRunner(final Rule rule)
    {
        this(rule, null);
    }

    /**
     * Creates a new ErrorLocatingParseRunner instance for the given rule.
     * The given MatchHandler is used as a delegate for the actual match handling.
     *
     * @param rule the parser rule
     * @param inner another MatchHandler to delegate the actual match handling
     * to, can be null
     */
    public ErrorLocatingParseRunner(final Rule rule,
        @Nullable final MatchHandler inner)
    {
        super(rule);
        this.inner = inner;
    }

    @Override
    public ParsingResult<V> run(final InputBuffer inputBuffer)
    {
        Preconditions.checkNotNull(inputBuffer, "inputBuffer");
        resetValueStack();
        errorIndex = 0;

        // run without fast string matching to properly get the error location
        final MatcherContext<V> rootContext
            = createRootContext(inputBuffer, this, false);
        final boolean matched = match(rootContext);
        if (!matched)
            parseErrors.add(new BasicParseError(inputBuffer, errorIndex, null));

        return createParsingResult(matched, rootContext);
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final boolean condition = inner == null
            ? context.getMatcher().match(context)
            : inner.match(context);
        if (!condition)
            return false;
        if (errorIndex < context.getCurrentIndex() && notTestNot(context))
            errorIndex = context.getCurrentIndex();

        return true;
    }

    private static boolean notTestNot(final MatcherContext<?> context)
    {
        if (context.getMatcher() instanceof TestNotMatcher)
            return false;
        return context.getParent() == null || notTestNot(context.getParent());
    }
}