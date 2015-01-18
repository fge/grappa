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

package com.github.parboiled1.grappa.run;

import com.github.parboiled1.grappa.buffers.CharSequenceInputBuffer;
import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.internal.NonFinalForTesting;
import com.github.parboiled1.grappa.matchers.base.Matcher;
import com.github.parboiled1.grappa.stack.DefaultValueStack;
import com.github.parboiled1.grappa.stack.ValueStack;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.parboiled.DefaultMatcherContext;
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.errors.ParseError;
import org.parboiled.support.ParsingResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParseRunner<V>
    implements ParseRunner<V>
{
    protected final Matcher rootMatcher;
    // TODO: make final
    protected List<ParseError> parseErrors = Lists.newArrayList();
    protected ValueStack<V> valueStack = new DefaultValueStack<>();
    protected Object initialValueStackSnapshot;

    protected AbstractParseRunner(@Nonnull final Rule rule)
    {
        rootMatcher = Preconditions.checkNotNull((Matcher) rule, "rule");
    }

    public final ValueStack<V> getValueStack()
    {
        return valueStack;
    }

    @Override
    public final ParsingResult<V> run(final CharSequence input)
    {
        Preconditions.checkNotNull(input, "input");
        return run(new CharSequenceInputBuffer(input));
    }

    @Override
    public final ParsingResult<V> run(final char[] input)
    {
        Preconditions.checkNotNull(input, "input");
        return run(new CharSequenceInputBuffer(input));
    }

    protected final void resetValueStack()
    {
        // FIXME: hack
        if (initialValueStackSnapshot == null)
            initialValueStackSnapshot = new ArrayList<>();
        valueStack.restoreSnapshot(initialValueStackSnapshot);
    }

    @NonFinalForTesting
    protected MatcherContext<V> createRootContext(
        final InputBuffer inputBuffer, final MatchHandler matchHandler)
    {
        return new DefaultMatcherContext<>(inputBuffer, valueStack,
            parseErrors, matchHandler, rootMatcher);
    }

    @NonFinalForTesting
    protected ParsingResult<V> createParsingResult(final boolean matched,
        final MatcherContext<V> rootContext)
    {
        return new ParsingResult<>(matched, rootContext.getNode(), valueStack,
            parseErrors, rootContext.getInputBuffer());
    }
}