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

package com.github.fge.grappa.run;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.context.DefaultMatcherContext;
import com.github.fge.grappa.run.context.MatcherContext;
import com.github.fge.grappa.run.events.MatchContextEvent;
import com.github.fge.grappa.run.events.MatchFailureEvent;
import com.github.fge.grappa.run.events.MatchSuccessEvent;
import com.github.fge.grappa.run.events.PostParseEvent;
import com.github.fge.grappa.run.events.PreMatchEvent;
import com.github.fge.grappa.run.events.PreParseEvent;
import com.github.fge.grappa.run.trace.TracingListener;
import com.github.fge.grappa.stack.ArrayValueStack;
import com.github.fge.grappa.stack.ValueStack;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Class to run a parser on an input, and retrieve a result
 *
 * <p>You can also register listeners which will be listening for one, or more,
 * parse events. Those listeners must implement {@link ParseEventListener}.</p>
 *
 * <p>For instance, the provided {@link TracingListener} will collect all parse
 * failues and success to construct a complete picture of the parsing process.
 * </p>
 *
 * @param <V> type parameter of the parser's stack values
 */
public class ParseRunner<V>
    implements MatchHandler
{
    // TODO: does it need to be volatile?
    private volatile Throwable throwable = null;

    private final EventBus bus = new EventBus(new SubscriberExceptionHandler()
    {
        @Override
        public void handleException(final Throwable exception,
            final SubscriberExceptionContext context)
        {
            if (throwable == null)
                throwable = exception;
            else
                throwable.addSuppressed(exception);
        }
    });


    protected final Matcher rootMatcher;
    protected ValueStack<V> valueStack;
    protected Object stackSnapshot;

    public ParseRunner(@Nonnull final Rule rule)
    {
        rootMatcher = Objects.requireNonNull((Matcher) rule, "rule");
    }

    public final ParsingResult<V> run(final CharSequence input)
    {
        Objects.requireNonNull(input, "input");
        return run(new CharSequenceInputBuffer(input));
    }

    public final ParsingResult<V> run(final InputBuffer inputBuffer)
    {
        Objects.requireNonNull(inputBuffer, "inputBuffer");
        resetValueStack();

        final MatcherContext<V> context = createRootContext(inputBuffer, this);
        bus.post(new PreParseEvent<>(context));

        if (throwable != null)
            throw new GrappaException("parsing listener error (before parse)",
                throwable);

        final boolean matched = context.runMatcher();
        final ParsingResult<V> result
            = createParsingResult(matched, context);

        bus.post(new PostParseEvent<>(result));

        if (throwable != null)
            throw new GrappaException("parsing listener error (after parse)",
                throwable);

        return result;
    }


    private void resetValueStack()
    {
        // TODO: write a "memoizing" API
        valueStack = new ArrayValueStack<>();
        stackSnapshot = null;
    }

    @NonFinalForTesting
    protected MatcherContext<V> createRootContext(
        final InputBuffer inputBuffer, final MatchHandler matchHandler)
    {
        return new DefaultMatcherContext<>(inputBuffer, valueStack,
            matchHandler, rootMatcher);
    }

    @NonFinalForTesting
    protected ParsingResult<V> createParsingResult(final boolean matched,
        final MatcherContext<V> rootContext)
    {
        return new ParsingResult<>(matched, valueStack,
            rootContext.getInputBuffer());
    }

    // TODO: replace with a supplier mechanism
    public final void registerListener(final ParseEventListener<V> listener)
    {
        bus.register(listener);
    }

    @Override
    public <T> boolean match(final MatcherContext<T> context)
    {
        final Matcher matcher = context.getMatcher();

        final PreMatchEvent<T> preMatchEvent = new PreMatchEvent<>(context);
        bus.post(preMatchEvent);

        if (throwable != null)
            throw new GrappaException("parsing listener error (before match)",
                throwable);

        // FIXME: is there any case at all where context.getMatcher() is null?
        @SuppressWarnings("ConstantConditions")
        final boolean match = matcher.match(context);

        final MatchContextEvent<T> postMatchEvent = match
            ? new MatchSuccessEvent<>(context)
            : new MatchFailureEvent<>(context);

        bus.post(postMatchEvent);

        if (throwable != null)
            throw new GrappaException("parsing listener error (after match)",
                throwable);

        return match;
    }
}
