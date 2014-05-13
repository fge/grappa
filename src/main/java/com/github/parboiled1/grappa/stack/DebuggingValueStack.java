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

package com.github.parboiled1.grappa.stack;

import com.github.parboiled1.grappa.misc.SystemOutCharSink;
import com.google.common.base.Joiner;
import com.google.common.io.CharSink;
import org.parboiled.support.ValueStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

// TODO: 1.1: change thrown exceptions!
public final class DebuggingValueStack<V>
    extends ForwardingValueStack<V>
{
    private static final Joiner JOINER = Joiner.on(", ");
    private final CharSink sink;

    public DebuggingValueStack(@Nonnull final ValueStack<V> delegate,
        @Nonnull final CharSink sink)
    {
        super(delegate);
        this.sink = sink;
    }

    public DebuggingValueStack(@Nonnull final ValueStack<V> delegate)
    {
        this(delegate, SystemOutCharSink.INSTANCE);
    }

    @Override
    public void clear()
    {
        log("clear");
        super.clear();
    }

    @Override
    public void restoreSnapshot(@Nullable final Object snapshot)
    {
        log("restore snapshot: " + snapshot);
        super.restoreSnapshot(snapshot);
    }

    @Override
    public void push(@Nullable final V value)
    {
        log("push: " + value);
        super.push(value);
    }

    @Override
    public void push(final int down, @Nullable final V value)
    {
        log("push " + down + ": " + value);
        super.push(down, value);
    }

    @Nullable
    @Override
    public V pop(final int down)
    {
        log("pop: " + down);
        return super.pop(down);
    }

    @Override
    public void poke(final int down, @Nullable final V value)
    {
        log("poke " + down + ": " + value);
        super.poke(down, value);
    }

    @Override
    public void pushAll(@Nullable final V firstValue,
        @Nullable final V... moreValues)
    {
        log("pushAll: " + JOINER.join(firstValue, moreValues));
        super.pushAll(firstValue, moreValues);
    }

    @Nullable
    @Override
    public Object takeSnapshot()
    {
        log("take snapshot");
        return super.takeSnapshot();
    }

    @Nullable
    @Override
    public V pop()
    {
        log("pop");
        return super.pop();
    }

    @Nullable
    @Override
    public V peek()
    {
        log("peek");
        return super.peek();
    }

    @Nullable
    @Override
    public V peek(final int down)
    {
        log("peek " + down);
        return super.peek(down);
    }

    @Override
    public void poke(@Nullable final V value)
    {
        log("poke: " + value);
        super.poke(value);
    }

    @Override
    public void dup()
    {
        log("dup");
        super.dup();
    }

    @Override
    public void swap(final int n)
    {
        log("swap: " + n);
        super.swap(n);
    }

    @Override
    public void swap()
    {
        log("swap");
        super.swap();
    }

    @Override
    public void swap3()
    {
        log("swap3");
        super.swap3();
    }

    @Override
    public void swap4()
    {
        log("swap4");
        super.swap4();
    }

    @Override
    public void swap5()
    {
        log("swap5");
        super.swap5();
    }

    @Override
    public void swap6()
    {
        log("swap6");
        super.swap6();
    }

    @Override
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        log("pushAll: " + JOINER.join(values));
        super.pushAll(values);
    }

    private void log(final CharSequence sequence)
    {
        try {
            sink.write(sequence);
        } catch (IOException e) {
            throw new RuntimeException("cannot write to debug channel", e);
        }
    }
}
