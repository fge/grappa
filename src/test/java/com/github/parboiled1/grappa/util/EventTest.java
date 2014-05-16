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

package com.github.parboiled1.grappa.util;

import com.github.parboiled1.grappa.matchers.EventBusMatcher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.parboiled.MatcherContext;
import org.parboiled.matchers.AnyMatcher;
import org.parboiled.matchers.Matcher;

import java.lang.reflect.Constructor;

public final class EventTest
{
    public static void main(final String... args)
        throws NoSuchMethodException
    {
        final EventBus bus = new EventBus("test");
        final Matcher delegate = new AnyMatcher();
        final Constructor<?> constructor
            = MyEvent.class.getConstructor(MatcherContext.class);
        final Matcher matcher = new EventBusMatcher(delegate, bus, "hello",
            constructor);
        bus.register(new MyListener());
        final MatcherContext<Object> context = new MatcherContextBuilder()
            .withInput("a").withMatcher(matcher).build();
        matcher.match(context);
    }

    public static final class MyEvent
    {
        private final MatcherContext<?> context;

        public MyEvent(final MatcherContext<?> context)
        {
            this.context = context;
        }
    }

    private static final class MyListener
    {
        @Subscribe
        public void hit(final MyEvent event)
        {
            final MatcherContext<?> context = event.context;
            System.out.println(context.getMatch());
            System.out.println(context.getLevel());
        }
    }

}

