/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package org.parboiled.support;

import com.github.parboiled1.grappa.cleanup.WillBePrivate;
import com.google.common.base.Joiner;
import org.parboiled.common.ConsoleSink;
import org.parboiled.common.Sink;

import java.util.Deque;
import java.util.LinkedList;

public class DebuggingValueStack<V>
    extends DefaultValueStack<V>
{
    private static final Joiner COMMA = Joiner.on(", ");

    @WillBePrivate(version = "1.1")
    public final Sink<String> log;

    public DebuggingValueStack()
    {
        this(new ConsoleSink());
    }

    public DebuggingValueStack(final Sink<String> log)
    {
        this.log = log;
    }

    public DebuggingValueStack(final Iterable<V> values)
    {
        this(values, new ConsoleSink());
    }

    public DebuggingValueStack(final Iterable<V> values, final Sink<String> log)
    {
        super(values);
        this.log = log;
    }

    @Override
    public void clear()
    {
        // TODO: that NULL again! Get rid of it!
        if (head != null) {
            super.clear();
            log("clear");
        }
    }

    @Override
    public void restoreSnapshot(final Object snapshot)
    {
        if (head == null && snapshot == null || head != null && head
            .equals(snapshot))
            return;
        super.restoreSnapshot(snapshot);
        log("restoreSnapshot");
    }

    @Override
    public void push(final V value)
    {
        super.push(value);
        log("push");
    }

    @Override
    public void push(final int down, final V value)
    {
        super.push(down, value);
        log("push");
    }

    @Override
    public V pop(final int down)
    {
        final V v = super.pop(down);
        log("pop");
        return v;
    }

    @Override
    public void poke(final int down, final V value)
    {
        super.poke(down, value);
        log("poke");
    }

    @Override
    public void swap()
    {
        super.swap();
        log("swap");
    }

    @Override
    public void swap3()
    {
        super.swap3();
        log("swap3");
    }

    @Override
    public void swap4()
    {
        super.swap4();
        log("swap4");
    }

    @Override
    public void swap5()
    {
        super.swap5();
        log("swap5");
    }

    @Override
    public void swap6()
    {
        super.swap6();
        log("swap6");
    }

    protected void log(final String action)
    {
        log.receive(action);
        log.receive(Chars.repeat(' ', 15 - action.length()));
        log.receive(": ");
        final Deque<V> elements = new LinkedList<V>();
        for (final V v: this)
            elements.addFirst(v);
        log.receive(COMMA.join(elements));
        log.receive("\n");
    }
}
