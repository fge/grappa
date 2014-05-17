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

import com.github.parboiled1.grappa.annotations.Experimental;
import com.github.parboiled1.grappa.exceptions.GrappaException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.matchers.ActionMatcher;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * A basic parser with an attached {@link EventBus}
 *
 * <p>This parser allows you to register events and listeners.</p>
 *
 * <p>Events are registered by a unique name (which must not be null), using
 * the {@link #addEvent(String, Class)} method; the class passed as an argument
 * <strong>must</strong> have a constructor accepting a {@link Context} as an
 * argument; grappa provides one such class, {@link BasicMatchEvent}, which
 * grabs the match of the current context (see {@link Context#getMatch()}).</p>
 *
 * <p>Note that the {@code addEvent()} method is both {@code protected} and
 * {@code final}; the recommended use is to add events in the constructor; for
 * instance:</p>
 *
 * <pre>
 *     public class MyParser
 *         extends EventBusParser&lt;Object&gt;
 *     {
 *         MyParser()
 *         {
 *             addEvent("basicEvent", BasicMatchEvent.class);
 *             addEvent("myEvent", MyEvent.class);
 *         }
 *     }
 * </pre>
 *
 * <p>Listeners are classes of yours; methods which will listen on events need
 * to be annotated with {@link Subscribe}; they must also be {@code public} and
 * accept only one argument, which is the event class. For instance:</p>
 *
 * <pre>
 *     public final class MyListener
 *     {
 *         private String match;
 *
 *         &#64;Subscribe
 *         public void receiveMatch(final BasicMatchEvent event)
 *         {
 *             match = event.getMatch();
 *         }
 *     }
 * </pre>
 *
 * <p>You then register instances of your listeners on your generated parser
 * using the {@link #addListener(Object)} method:</p>
 *
 * <pre>
 *     final MyParser parser = Parboiled.createParser(MyParser.class);
 *     final MyListener listener = new MyListener();
 *     parser.addListener(listener);
 * </pre>
 *
 * <p>In order to get events to be published in the parsing process, you will
 * use the {@link #fireEvent(String)} method; the argument to this method must
 * be a name you have registered using {@link #addEvent(String, Class)
 * addEvent()}. Given the above, you will therefore write, for instance:</p>
 *
 * <pre>
 *     Rule wantToCaptureThat()
 *     {
 *         return sequence(string("Capture me!"), fireEvent("basicEvent"));
 *     }
 * </pre>
 *
 * <p>This will cause the parser to create a new instance of the class
 * associated with this name with the current parser context as a constructor
 * argument, which it will then publish via the event bus.</p>
 *
 * <p>Note that you are not limited to one subscriber per event class. For the
 * full details of how, and when, events are dispatched, see the {@link EventBus
 * javadoc for {@code EventBus}}.</p>
 *
 * @param <V> the result type of the parser
 *
 * @see EventBus
 * @see BasicMatchEvent
 */
@Experimental
public abstract class EventBusParser<V>
    extends BaseParser<V>
{
    protected final EventBus bus = new EventBus();
    private final Map<String, Constructor<?>> eventMap = Maps.newHashMap();

    /**
     * Associate an event class to a given name
     *
     * <p>Note that at this moment, if you register an event twice with the
     * same name, the previous event class is overriden without notice.</p>
     *
     * <p>Note also that grappa makes no effort to modify the visibility of
     * constructors, nor does it check it; it is therefore your responsibility
     * to ensure that both the class and constructor have sufficient "access
     * privileges".</p>
     *
     * @param eventName the name to associate this event to
     * @param eventClass the event class
     * @throws GrappaException no suitable constructor found
     *
     * @see BasicMatchEvent
     * @see #fireEvent(String)
     */
    protected final void addEvent(@Nonnull final String eventName,
        @Nonnull final Class<?> eventClass)
    {
        Preconditions.checkNotNull(eventName);
        Preconditions.checkNotNull(eventClass);
        final Constructor<?> constructor;
        try {
            constructor = eventClass.getConstructor(Context.class);
            eventMap.put(eventName, constructor);
        } catch (NoSuchMethodException e) {
            throw new GrappaException("cannot find constructor for event class",
                e);
        }
    }

    /**
     * Register a listener to the event bus
     *
     * @param listener the listener
     *
     * @see EventBus#register(Object)
     */
    public final void addListener(@Nonnull final Object listener)
    {
        bus.register(Preconditions.checkNotNull(listener));
    }

    /**
     * Fire an event by name
     *
     * <p>See the class description for more details.</p>
     *
     * @param eventName the name of the event
     * @return always true
     *
     * @see ActionMatcher
     * @see Action
     */
    public boolean fireEvent(@Nonnull final String eventName)
    {
        Preconditions.checkNotNull(eventName);

        final Constructor<?> constructor = eventMap.get(eventName);
        if (constructor == null)
            throw new GrappaException("no event class for name " + eventName);

        final Object event;
        try {
            event = constructor.newInstance(getContext());
        } catch (InstantiationException e) {
            throw new GrappaException("cannot instantiate event class", e);
        } catch (IllegalAccessException e) {
            throw new GrappaException("cannot instantiate event class", e);
        } catch (InvocationTargetException e) {
            throw new GrappaException("cannot instantiate event class", e);
        }

        bus.post(event);
        return true;
    }
}
