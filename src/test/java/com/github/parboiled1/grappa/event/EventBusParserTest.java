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

package com.github.parboiled1.grappa.event;

import com.google.common.eventbus.Subscribe;
import org.mockito.ArgumentCaptor;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.BasicParseRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public final class EventBusParserTest
{
    private static class DummyParser
        extends EventBusParser<Object>
    {
        protected DummyParser()
        {
            addEvent("event", BasicMatchEvent.class);
        }

        Rule matchHere()
        {
            return sequence('a', fireEvent("event"));
        }

        Rule noMatchHere()
        {
            return sequence('b', fireEvent("event"));
        }
    }

    private static class DummyListener
    {
        @Subscribe
        public void receive(final BasicMatchEvent<?> ignored)
        {
        }
    }

    private DummyParser parser;
    private DummyListener listener;

    @BeforeMethod
    public void init()
    {
        listener = spy(new DummyListener());
        parser = Parboiled.createParser(DummyParser.class);
        parser.addListener(listener);
    }

    @Test
    public void subscriberIsCalledOnMatch()
    {
        new BasicParseRunner<Object>(parser.matchHere()).run("a");

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<BasicMatchEvent> captor
            = ArgumentCaptor.forClass(BasicMatchEvent.class);

        verify(listener).receive(captor.capture());
        verifyNoMoreInteractions(listener);

        assertThat(captor.getValue().getMatch())
            .as("captured content is correct").isEqualTo("a");
    }

    @Test
    public void subscriberIsNotCalledIfNoMatch()
    {
        new BasicParseRunner<Object>(parser.noMatchHere()).run("a");

        verify(listener, never()).receive(any(BasicMatchEvent.class));
    }
}
