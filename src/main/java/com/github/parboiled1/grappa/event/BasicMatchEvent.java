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
import com.github.parboiled1.grappa.helpers.ValueBuilder;
import org.parboiled.Context;

/**
 * A basic match event to be used with an {@link EventBusParser}
 *
 * <p>Reminder: in order for an event class to be usable by the parser, it must
 * have a constructor with a {@link Context} as an argument.</p>
 *
 * <p>Note that since instances of event classes are constructed in an action
 * context, this means you can call all of the methods of {@link Context} in
 * the constructor (get the current match, index, peek into the stack etc) --
 * <strong>but not anywhere else!!</strong></p>
 *
 * <p>The latter basically means: DO NOT make the {@code Context} an instance
 * variable of your event class; call any method of it outside of the
 * constructor and it will fail very badly. You have been warned!</p>
 *
 * <p>This simple match event only grabs the text matched by the previous rule
 * (via {@link Context#getMatch()}.</p>
 *
 * @param <V> type of the parser production
 *
 * @deprecated use a {@link ValueBuilder} and {@link
 * EventBusParser#buildEvent(ValueBuilder)} instead
 */
@Deprecated
@Experimental
public class BasicMatchEvent<V>
{
    private final String match;

    public BasicMatchEvent(final Context<V> context)
    {
        match = context.getMatch();
    }

    public final String getMatch()
    {
        return match;
    }
}
