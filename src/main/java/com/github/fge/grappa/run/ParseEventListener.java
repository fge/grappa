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

package com.github.fge.grappa.run;

import com.github.fge.grappa.run.events.MatchFailureEvent;
import com.github.fge.grappa.run.events.MatchSuccessEvent;
import com.github.fge.grappa.run.events.PostParseEvent;
import com.github.fge.grappa.run.events.PreMatchEvent;
import com.github.fge.grappa.run.events.PreParseEvent;
import com.github.fge.grappa.run.trace.TracingListener;
import com.google.common.eventbus.Subscribe;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A listener which you can register to a {@link ParseRunner}
 *
 * <p>You can register listeners at five different points in the parsing
 * process:</p>
 *
 * <ul>
 *     <li>before the parsing starts;</li>
 *     <li>before a match is attempted;</li>
 *     <li>after a match is attempted, successfully or not (those are two
 *     separate events);</li>
 *     <li>after the parsing is completed.</li>
 * </ul>
 *
 * <p>One example implementation provided with grappa is the {@link
 * TracingListener}, which records all parsing events and produces a zip file
 * which can then be used with the <a href="https://github.com/fge/grappa-debugger"
 * target="_blank">debugger</a>.</p>
 *
 * <p>All the default implementations of methods in this class do nothing. A
 * call to {@code super()} in implementations is therefore not necessary.</p>
 *
 * @param <V> the type parameter of the parser's stack values
 *
 * @see ParseRunner#registerListener(ParseEventListener)
 */
@ParametersAreNonnullByDefault
public class ParseEventListener<V>
{
    /**
     * Method called before the parsing process starts
     *
     * @param event the event
     */
    @Subscribe
    public void beforeParse(final PreParseEvent<V> event)
    {
    }

    /**
     * Method called before a match is attempted
     *
     * @param event the event
     */
    @Subscribe
    public void beforeMatch(final PreMatchEvent<V> event)
    {
    }

    /**
     * Method called when a match is completed with success
     *
     * @param event the event
     */
    @Subscribe
    public void matchSuccess(final MatchSuccessEvent<V> event)
    {
    }

    /**
     * Method called when a match has resulted in a failure
     *
     * @param event the event
     */
    @Subscribe
    public void matchFailure(final MatchFailureEvent<V> event)
    {
    }

    /**
     * Method called after the parsing process is complete
     *
     * @param event the event
     */
    @Subscribe
    public void afterParse(final PostParseEvent<V> event)
    {
    }
}
