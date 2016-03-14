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

package com.github.fge.grappa.parsers;

import com.github.fge.grappa.helpers.ValueBuilder;
import com.github.fge.grappa.support.Var;
import com.google.common.eventbus.EventBus;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Obsolete!
 *
 * @deprecated will disappear in 2.1.x. No real value. Use {@link
 * EventBusParser} instead
 */
@Deprecated
public abstract class ListeningParser<V>
    extends BaseParser<V>
{
    protected final EventBus bus = new EventBus();

    public final boolean register(@Nonnull final Object listener)
    {
        bus.register(Objects.requireNonNull(listener));
        return true;
    }

    public final boolean unregister(@Nonnull final Object listener)
    {
        bus.unregister(Objects.requireNonNull(listener));
        return true;
    }

    public final <T> boolean post(@Nonnull final ValueBuilder<T> builder)
    {
        Objects.requireNonNull(builder);

        final T event = builder.build();
        bus.post(event);
        builder.reset();
        return true;
    }

    public final <T> boolean post(@Nonnull final Var<T> var)
    {
        Objects.requireNonNull(var);
        @SuppressWarnings("ConstantConditions")
        final T value = Objects.requireNonNull(var.get());
        bus.post(value);
        return true;
    }

    public final boolean postRaw(@Nonnull final Object object)
    {
        Objects.requireNonNull(object);
        bus.post(object);
        return true;
    }
}
