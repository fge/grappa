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
import com.github.parboiled1.grappa.helpers.ValueBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
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
 * <p><strong>TODO: redocument</strong></p>
 *
 * @param <V> the result type of the parser
 *
 * @see ValueBuilder
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
     *
     * @deprecated use {@link #buildEvent(ValueBuilder)} instead
     */
    @Deprecated
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
     *
     * @deprecated use {@link #buildEvent(ValueBuilder)} instead
     */
    @Deprecated
    public final boolean fireEvent(@Nonnull final String eventName)
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

    /**
     * Send an event on the bus which is the result of the value builder's
     * production
     *
     * <p>This method will {@link ValueBuilder#build() build} the value and
     * {@link EventBus#post(Object) post} the built value on the bus.</p>
     *
     * <p>Note that it <strong>will not</strong> reset the builder, that is, it
     * will not call {@link ValueBuilder#reset()} after it has built the value;
     * resetting the value if necessary is the responsibility of the caller.</p>
     *
     * @param builder the value builder
     * @param <T> the value type produced by the builder
     * @return always {@code true}
     */
    public final  <T> boolean buildEvent(@Nonnull final ValueBuilder<T> builder)
    {
        Preconditions.checkNotNull(builder);

        final T event = builder.build();
        bus.post(event);
        return true;
    }
}
