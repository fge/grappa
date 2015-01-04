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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public class ForwardingValueStack<V>
    implements ValueStack<V>
{
    protected final ValueStack<V> delegate;

    public ForwardingValueStack(@Nonnull final ValueStack<V> delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    @Override
    public int size()
    {
        return delegate.size();
    }

    @Override
    public void clear()
    {
        delegate.clear();
    }

    @Override
    @Nullable
    public Object takeSnapshot()
    {
        return delegate.takeSnapshot();
    }

    @Override
    public void restoreSnapshot(@Nullable final Object snapshot)
    {
        delegate.restoreSnapshot(snapshot);
    }

    @Override
    public void push(@Nullable final V value)
    {
        delegate.push(value);
    }

    @Override
    public void push(final int down, @Nullable final V value)
    {
        delegate.push(down, value);
    }

    @Override
    public void pushAll(@Nullable final V firstValue,
        @Nullable final V... moreValues)
    {
        delegate.pushAll(firstValue, moreValues);
    }

    @Override
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        delegate.pushAll(values);
    }

    @Override
    @Nullable
    public V pop()
    {
        return delegate.pop();
    }

    @Override
    @Nullable
    public V pop(final int down)
    {
        return delegate.pop(down);
    }

    @Override
    @Nullable
    public V peek()
    {
        return delegate.peek();
    }

    @Override
    @Nullable
    public V peek(final int down)
    {
        return delegate.peek(down);
    }

    public void poke(@Nullable final V value)
    {
        delegate.poke(value);
    }

    public void poke(final int down, @Nullable final V value)
    {
        delegate.poke(down, value);
    }

    @Override
    public void dup()
    {
        delegate.dup();
    }

    @Override
    public void swap(final int n)
    {
        delegate.swap(n);
    }

    @Override
    public void swap()
    {
        delegate.swap();
    }

    @Override
    public void swap3()
    {
        delegate.swap3();
    }

    @Override
    public void swap4()
    {
        delegate.swap4();
    }

    @Override
    public void swap5()
    {
        delegate.swap5();
    }

    @Override
    public void swap6()
    {
        delegate.swap6();
    }

    @Override
    public Iterator<V> iterator()
    {
        return delegate.iterator();
    }
}
