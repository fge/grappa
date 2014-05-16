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

package com.github.parboiled1.grappa;

import com.github.parboiled1.grappa.event.BasicMatchEvent;
import com.github.parboiled1.grappa.event.EventBusParser;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.TracingParseRunner;

import javax.annotation.Nonnull;
import javax.annotation.Untainted;

public final class Moo
{
    public static final class StringListener<V>
    {
        private final StringBuilder sb = new StringBuilder();

        @Subscribe
        public void capture(@Untainted @Nonnull final BasicMatchEvent<V> event)
        {
            System.out.println("Called");
            sb.append(event.getMatch());
        }

        public String getContent()
        {
            return sb.toString();
        }
    }

    static class MyParser
        extends EventBusParser<Object>
    {
        MyParser()
        {
            addEvent("normal", BasicMatchEvent.class);
            addEvent("special", BasicMatchEvent.class);
        }

        public final void addListener(@Nonnull final Object listener)
        {
            bus.register(Preconditions.checkNotNull(listener));
        }

        Rule normal()
        {
            return sequence(oneOrMore(noneOf("\\\"")), fireEvent("normal"));
        }

        Rule special()
        {
            return sequence('\\', ANY, fireEvent("special"));
        }

        Rule content()
        {
            return join(normal()).using(special()).min(0);
        }

        Rule rule()
        {
            return sequence('"', content(), '"', EOI);
        }
    }

    public static void main(final String... args)
    {
        final StringListener listener = new StringListener();

        final MyParser parser = Parboiled.createParser(MyParser.class);

        parser.addListener(listener);

        final ParseRunner<Object> runner
            = new TracingParseRunner<Object>(parser.rule());
        System.out.println(runner.run("\"Hello \\\" world\"").hasErrors());

        System.out.println(listener.getContent());
    }
}
