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
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.parboiled.BaseParser;
import org.parboiled.support.Var;

import javax.annotation.Nonnull;

/**
 * A basic parser with an attached {@link EventBus}
 *
 * <p>This parser allows you to post either {@link Var}s or {@link
 * ValueBuilder}s on the bus; methods of your custom classes having {@link
 * Subscribe}d to receive the correct values will then be invoked by the bus
 * with the given arguments:</p>
 *
 * <ul>
 *     <li>the result of {@link Var#get()} for vars;</li>
 *     <li>the result of {@link ValueBuilder#build()} for value builders.</li>
 * </ul>
 *
 * <p>A subscribing method must be {@code public} and accept only one argument,
 * the type of which is the value type posted by the event <em>or any
 * subtype</em> (this means, for instance, that a subscribing method accepting a
 * {@link Number} as an argument will also receive {@link Integer}s or {@link
 * Double}s).</p>
 *
 * <p>A simple example class would be:</p>
 *
 * <pre>
 *     public final class MyClass
 *     {
 *         private String myString;
 *
 *         &#64;Subscribe
 *         public void setMyString(@Nonnull final String s)
 *         {
 *             myString = s;
 *         }
 *     }
 * </pre>
 *
 * <p>You would then register an instance of that class with your parser (using
 * {@link #register(Object)}) and would write rules like the following:</p>
 *
 * <pre>
 *     protected final Var&lt;String&gt; var = new Var&lt;String&gt;
 *     protected final ValueBuilder&lt;String&gt; builder
 *         = new ValueBuilder&lt;String&gt;
 *
 *     Rule usingVar()
 *     {
 *         return sequence(oneOrMore('a'), var.set(match()), post(var));
 *     }
 *
 *     Rule usingBuilder()
 *     {
 *         return sequence(oneOrMore('a'), builder.set(match()),
 *             post(builder));
 *     }
 * </pre>
 *
 * <p>Note that if using a var, the value <strong>must not</strong> be null.
 * </p>
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

    /**
     * Register a listener to the event bus
     *
     * @param listener the listener
     *
     * @see EventBus#register(Object)
     */
    public final void register(@Nonnull final Object listener)
    {
        bus.register(Preconditions.checkNotNull(listener));
    }

    /**
     * Post a value on the bus from a {@link ValueBuilder}
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
    public final <T> boolean post(@Nonnull final ValueBuilder<T> builder)
    {
        Preconditions.checkNotNull(builder);

        final T event = builder.build();
        bus.post(event);
        return true;
    }

    /**
     * Post a value on the bus from a {@link Var}
     *
     * <p>This method will {@link Var#get() get} the value of the associated
     * var and {@link EventBus#post(Object) post} it on the bus.</p>
     *
     * <p>Notes:</p>
     *
     * <ul>
     *     <li>the value <strong>must not be null</strong>;</li>
     *     <li>this method will not affect the var in any way (ie, it will leave
     *     the existing value intact etc).</li>
     * </ul>
     *
     * @param var the var to use
     * @param <T> value type of the var
     * @return always true
     */
    public final <T> boolean post(@Nonnull final Var<T> var)
    {
        Preconditions.checkNotNull(var);
        @SuppressWarnings("ConstantConditions")
        final T value = Preconditions.checkNotNull(var.get());
        bus.post(value);
        return true;
    }

    /**
     * "Raw" post to the bus
     *
     * <p>Use this method if you want to post any other object than a value
     * wrapped in a {@code Var} or {@code ValueBuilder}.</p>
     *
     * @param object the object (must not be null)
     * @return always true
     */
    public final boolean postRaw(@Nonnull final Object object)
    {
        Preconditions.checkNotNull(object);
        bus.post(object);
        return true;
    }
}
