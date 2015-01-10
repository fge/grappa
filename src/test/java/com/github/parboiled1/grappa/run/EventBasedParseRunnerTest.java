/*
 * Copyright (C) 2015 Francis Galiegue <fgaliegue@gmail.com>
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

import com.github.parboiled1.grappa.matchers.base.Matcher;
import com.google.common.eventbus.Subscribe;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.parboiled.MatcherContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class EventBasedParseRunnerTest
{
    private interface DummyListener
    {
        @Subscribe
        void pre(PreMatchEvent<?> event);

        @Subscribe
        void success(MatchSuccessEvent<?> event);

        @Subscribe
        void failure(MatchFailureEvent<?> event);
    }

    private MatcherContext<Object> context;
    private Matcher matcher;
    private DummyListener listener;
    private EventBasedParseRunner<Object> runner;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void init()
    {
        matcher = mock(Matcher.class);
        context = mock(MatcherContext.class);
        when(context.getMatcher()).thenReturn(matcher);
        listener = mock(DummyListener.class);
        runner = new EventBasedParseRunner<>(matcher);
        runner.registerListener(listener);
    }

    @Test
    public void preAndFailEventsAreDispatched()
    {
        final ArgumentCaptor<PreMatchEvent> pre
            = ArgumentCaptor.forClass(PreMatchEvent.class);
        final ArgumentCaptor<MatchFailureEvent> failure
            = ArgumentCaptor.forClass(MatchFailureEvent.class);
        final InOrder inOrder = inOrder(listener);

        // This is the default but let's make that explicit
        when(matcher.match(context)).thenReturn(false);

        assertThat(runner.match(context)).isFalse();

        inOrder.verify(listener).pre(pre.capture());
        inOrder.verify(listener).failure(failure.capture());
        inOrder.verifyNoMoreInteractions();

        assertThat(pre.getValue().context).isSameAs(context);
        assertThat(failure.getValue().context).isSameAs(context);
    }

    @Test
    public void preAndSuccessEventsAreDispatched()
    {
        final ArgumentCaptor<PreMatchEvent> pre
            = ArgumentCaptor.forClass(PreMatchEvent.class);
        final ArgumentCaptor<MatchSuccessEvent> success
            = ArgumentCaptor.forClass(MatchSuccessEvent.class);
        final InOrder inOrder = inOrder(listener);

        when(matcher.match(context)).thenReturn(true);

        assertThat(runner.match(context)).isTrue();

        inOrder.verify(listener).pre(pre.capture());
        inOrder.verify(listener).success(success.capture());
        inOrder.verifyNoMoreInteractions();

        assertThat(pre.getValue().context).isSameAs(context);
        assertThat(success.getValue().context).isSameAs(context);

    }
}