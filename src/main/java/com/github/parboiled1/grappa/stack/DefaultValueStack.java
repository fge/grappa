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

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

// TODO: 1.1: change thrown exceptions!
@ParametersAreNonnullByDefault
public final class DefaultValueStack<V>
    implements ValueStack<V>
{
    private List<V> stack = new ArrayList<>();

    @Override
    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    @Override
    public int size()
    {
        return stack.size();
    }

    @Override
    public void clear()
    {
        stack.clear();
    }

    @Nonnull
    @Override
    public Object takeSnapshot()
    {
        return new ArrayList<>(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void restoreSnapshot(final Object snapshot)
    {
        Preconditions.checkNotNull(snapshot);
        Preconditions.checkState(snapshot.getClass() == ArrayList.class);
        stack = (List<V>) snapshot;
    }

    @Override
    public void push(final V value)
    {
        push(0, value);
    }

    @Override
    public void push(final int down, final V value)
    {
        /*
         * It is legal to append at the end! We must therefore check that the
         * index - 1 is strictly less than size, not the index itself
         */
        checkAvailableIndex(down - 1);
        stack.add(down, Objects.requireNonNull(value));
    }

    @SafeVarargs
    @Override
    public final void pushAll(final V firstValue, final V... moreValues)
    {
        final int newSize = stack.size() + 1 + moreValues.length;
        final List<V> newStack = new ArrayList<>(newSize);

        newStack.add(Objects.requireNonNull(firstValue));
        for (final V value: moreValues)
            newStack.add(Objects.requireNonNull(value));
        newStack.addAll(stack);

        stack = newStack;
    }

    @Nonnull
    @Override
    public V pop()
    {
        checkAvailableIndex(0);
        return stack.remove(0);
    }

    @Nonnull
    @Override
    public V pop(final int down)
    {
        checkAvailableIndex(down);
        return stack.remove(down);
    }

    @Nonnull
    @Override
    public V peek()
    {
        checkAvailableIndex(0);
        return stack.get(0);
    }

    @Nonnull
    @Override
    public V peek(final int down)
    {
        checkAvailableIndex(down);
        return stack.get(down);
    }

    @Override
    public void poke(final V value)
    {
        poke(0, value);
    }

    @Override
    public void poke(final int down, final V value)
    {
        checkAvailableIndex(down);
        stack.set(down, Objects.requireNonNull(value));
    }

    @Override
    public void dup()
    {
        checkAvailableIndex(0);
        final V element = stack.get(0);
        stack.add(0, element);
    }

    @Override
    public void swap(final int n)
    {
        Preconditions.checkState(n >= 2, "illegal argument to swap() (" +
            n + "), must be 2 or greater");
        /*
         * As for .push(n, value), we need to check for n - 1 here
         */
        checkAvailableIndex(n - 1);
        Collections.reverse(stack.subList(0, n));
    }

    @Override
    public void swap()
    {
        swap(2);
    }

    @Override
    public Iterator<V> iterator()
    {
        return Iterators.unmodifiableIterator(stack.iterator());
    }

    @Nonnull
    @Override
    public String toString()
    {
        return stack.toString();
    }

    private void checkAvailableIndex(final int index)
    {
        Preconditions.checkState(index < stack.size(),
            "not enough elements in stack");
    }
}
