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

import com.github.parboiled1.grappa.annotations.DoNotUse;
import com.github.parboiled1.grappa.annotations.Unused;
import com.github.parboiled1.grappa.annotations.WillBeRemoved;
import com.github.parboiled1.grappa.misc.SinkAdapter;
import com.github.parboiled1.grappa.misc.SystemOutCharSink;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSink;
import org.parboiled.common.Sink;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Deprecated!
 *
 * @param <V> value type
 *
 * @deprecated use {@link
 * com.github.parboiled1.grappa.stack.DebuggingValueStack} instead
 */
@Deprecated
@WillBeRemoved(version = "1.1")
public class DebuggingValueStack<V>
    extends DefaultValueStack<V>
{
    private static final Joiner COMMA = Joiner.on(", ");

    @Nullable
    public final SinkAdapter log;
    private final CharSink sink;

    public DebuggingValueStack()
    {
        this(SystemOutCharSink.INSTANCE);
    }

    public DebuggingValueStack(final CharSink sink)
    {
        this.sink = sink;
        log = null;
    }

    public DebuggingValueStack(@Nonnull final Iterable<V> values,
        @Nonnull final CharSink sink)
    {
        super(values);
        this.sink = Preconditions.checkNotNull(sink);
        log = null;
    }

    @Deprecated
    @DoNotUse
    public DebuggingValueStack(final Sink<String> log)
    {
        sink = this.log = new SinkAdapter(log);
    }

    /**
     * Deprecated!
     *
     * <p>It is never used and should not be used.</p>
     *
     * @param values deprecated
     */
    @Deprecated
    @DoNotUse
    @Unused
    public DebuggingValueStack(final Iterable<V> values)
    {
        this(values, SystemOutCharSink.INSTANCE);
    }

    /**
     * Deprecated!
     *
     * <p>It is never used and should not be used.</p>
     *
     * @param values deprecated
     * @param log deprecated
     */
    @Deprecated
    @DoNotUse
    @Unused
    public DebuggingValueStack(final Iterable<V> values, final Sink<String> log)
    {
        super(values);
        sink = this.log = new SinkAdapter(log);
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

    /**
     * Reverses the order of the top n stack values
     *
     * @param n the number of elements to reverse
     * @throws IllegalStateException the stack does not contain at least n
     * elements
     */
    @Override
    public void swap(final int n)
    {
        log("swap: " + n);
        super.swap(n);
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
        try {
            sink.write(action);
            sink.write(Chars.repeat(' ', 15 - action.length()));
            sink.write(": ");
            final Deque<V> elements = new LinkedList<V>();
            for (final V v : this)
                elements.addFirst(v);
            sink.write(COMMA.join(elements));
            sink.write("\n");
        } catch (IOException e) {
            throw new RuntimeException("failed to write to CharSink", e);
        }
    }
}
