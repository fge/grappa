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
 * <p>Since Grappa allows you to build basically anything during a parsing run,
 * you may use it, for instance, to post elements you would push on the stack
 * "ahead of time". Or, even more simple, you'd just post an event each time you
 * have a match for a certain rule, with the text matched. For instance:</p>
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
 * <p>You can, of course, do more than that.</p>
 *
 * <p>The method to post on the bus ({@link #post(Object)}, of which there is
 * an example above) is basically an {@link Action}, and as such obeys all rules
 * of an action; unlike what happens with the parser stack however, if you are
 * in a predicate and allow actions, the objects you post on the bus cannot be
 * "rolled back", for obvious reasons. You may still detect whether you are in
 * an action and post anyway as follows:</p>
 *
 * <pre>
 *     public boolean postNoPredicate(final Object object)
 *     {
 *         if (!inPredicate())
 *             post(object);
 *         return true;
 *     }
 * </pre>
 *
 * <h2>Usage notes</h2>
 *
 * <p>You can only register listeners to such a parser once the parser has been
 * created, and they <em>must</em> be created before the parser is run on an
 * input. For instance:</p>
 *
 * <pre>
 *     // MyParser extends EventBusParser
 *     final MyParser parser = Grappa.createParser(MyParser.class);
 *
 *     final MyListener listener = new MyListener();
 *     parser.register(listener);
 *
 *     // Crete a runner, run on an input
 * </pre>
 *
 * <p>If you reuse the same parser again on different inputs, then your listener
 * will be reused again, so be careful about it being state dependent (this may
 * be a desired effect however; this is up to you to decide).</p>
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
