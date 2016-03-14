/*
 * Copyright (C) 2016 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.grappa.parsers;

import com.github.fge.grappa.annotations.SkipActionsInPredicates;
import com.github.fge.grappa.rules.Action;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A {@link BaseParser} with an attached {@link EventBus}
 *
 * <p>This parser allows you to register instances of classes with methods which
 * have subscribed to a bus using Guava's {@link Subscribe} annotation. For the
 * recall, such methods must obey the following criteria:</p>
 *
 * <ul>
 *     <li>they must be public,</li>
 *     <li>they must return {@code void},</li>
 *     <li>they must accept one, and only one, argument.</li>
 * </ul>
 *
 * <p>A sample usage is shown below, where an event is posted on the bus, with
 * the text matched as an argument:</p>
 *
 * <pre>
 *     public Rule someRule()
 *     {
 *         // will trigger on any subscribed method having a String as an
 *         // argument, since match() returns a String
 *         return sequence("foobar", post(match()));
 *     }
 * </pre>
 *
 * <p>This method ({@link #post(Object)} ultimately becomes an {@link Action},
 * which means it obeys all the rules of an action. This includes, for instance,
 * the presence (or absence thereof) of annotations such as {@link
 * SkipActionsInPredicates}.</p>
 *
 * <h2>Usage notes</h2>
 *
 * <p>Listeners to such a parser can only be registered once the parser has been
 * created. For instance:</p>
 *
 * <pre>
 *     // MyParser extends EventBusParser
 *     final MyParser parser = Grappa.createParser(MyParser.class);
 *
 *     // Create the listener
 *     final MyListener listener = new MyListener();
 *
 *     // Register it to the parser
 *     parser.register(listener);
 *
 *     // Crete a runner, run on an input
 * </pre>
 *
 * <p>Please note that such a listener's state, if any, will be retained across
 * reuses of the same parser on different inputs. According to the use case,
 * this may, or may not, be a desirable property.</p>
 *
 * @see Subscribe
 * @see EventBus#register(Object)
 * @see EventBus#post(Object)
 */
public abstract class EventBusParser<V>
    extends BaseParser<V>
{
    protected final EventBus bus = new EventBus();

    /**
     * Register a listener to the event bus
     *
     * @param listener the listener
     * @return always true
     *
     * @see EventBus#register(Object)
     */
    public final boolean register(@Nonnull final Object listener)
    {
        bus.register(Objects.requireNonNull(listener));
        return true;
    }

    /**
     * Post an arbitrary, non null, object on the bus
     *
     * @param object the object (must not be null)
     * @return always true
     */
    public final boolean post(@Nonnull final Object object)
    {
        Objects.requireNonNull(object);
        bus.post(object);
        return true;
    }
}
