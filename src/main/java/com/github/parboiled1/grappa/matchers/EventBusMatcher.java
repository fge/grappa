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

package com.github.parboiled1.grappa.matchers;

import com.github.parboiled1.grappa.exceptions.GrappaException;
import com.google.common.eventbus.EventBus;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.matchers.AbstractMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchervisitors.MatcherVisitor;

import javax.annotation.Untainted;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class EventBusMatcher
    extends AbstractMatcher
{
    private final Matcher delegate;
    private final EventBus bus;
    private final Constructor<?> constructor;

    public EventBusMatcher(final Rule delegate, final EventBus bus,
        final String eventName, @Untainted final Constructor<?> constructor)
    {
        super(delegate, "EVENT: " + eventName);
        this.delegate = getChildren().get(0);
        this.bus = bus;
        this.constructor = constructor;
    }
    /**
     * Tries a match on the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if the match was successful
     */
    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        if (!delegate.getSubContext(context).runMatcher())
            return false;

        bus.post(buildEvent(context));
        context.createNode();
        return true;
    }

    /**
     * Accepts the given matcher visitor.
     *
     * @param visitor the visitor
     * @return the value returned by the given visitor
     */
    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        return null;
    }

    private <V> Object buildEvent(final MatcherContext<V> ctx)
    {
        try {
            return constructor.newInstance(ctx);
        } catch (InstantiationException e) {
            throw new GrappaException("cannot instantiate event class", e);
        } catch (IllegalAccessException e) {
            throw new GrappaException("cannot instantiate event class", e);
        } catch (InvocationTargetException e) {
            throw new GrappaException("cannot instantiate event class", e);
        }
    }
}
