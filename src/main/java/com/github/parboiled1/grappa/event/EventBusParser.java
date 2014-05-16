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
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import org.parboiled.BaseParser;
import org.parboiled.Context;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Experimental
public abstract class EventBusParser<V>
    extends BaseParser<V>
{
    protected final EventBus bus = new EventBus();
    private final Map<String, Constructor<?>> eventMap = Maps.newHashMap();

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

    @Beta
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
