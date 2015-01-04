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
import com.google.common.collect.Lists;
import org.parboiled.errors.GrammarException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// TODO: 1.1: change thrown exceptions!
public final class DefaultValueStack<V>
    implements ValueStack<V>
{
    private List<V> stack = new ArrayList<V>();

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

    @Override
    public Object takeSnapshot()
    {
        return new ArrayList<V>(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void restoreSnapshot(final Object snapshot)
    {
        // FIXME: should not happen, but...
        // TODO: when old implementation is out, make snapshot arg @Nonnull
        if (snapshot == null) {
            stack = new ArrayList<V>();
            return;
        }
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
        try {
            checkSize(down - 1);
            stack.add(down, value);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void pushAll(@Nullable final V firstValue,
        @Nullable final V... moreValues)
    {
        // FIXME: hackish :/ Can throw ClassCastException all right
        if (firstValue instanceof Iterable
            && moreValues != null
            && moreValues.length == 0) {
            @SuppressWarnings("unchecked")
            final Iterable<V> values = (Iterable<V>) firstValue;
            pushAll(values);
            return;
        }
        final List<V> temp = new ArrayList<V>();
        temp.add(firstValue);
        if (moreValues == null)
            temp.add(null);
        else
            temp.addAll(Arrays.asList(moreValues));
        pushAll(temp);
    }

    @Override
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        final List<V> newStack = Lists.newArrayList(values);
        newStack.addAll(stack);
        stack = newStack;
    }

    @Override
    public V pop()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        return stack.remove(0);
    }

    @Override
    public V pop(final int down)
    {
        try {
            checkSize(down);
            return stack.remove(down);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public V peek()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        return stack.get(0);
    }

    @Override
    public V peek(final int down)
    {
        try {
            checkSize(down);
            return stack.get(down);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void poke(@Nullable final V value)
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        poke(0, value);
    }

    @Override
    public void poke(final int down, @Nullable final V value)
    {
        try {
            checkSize(down);
            stack.set(down, value);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void dup()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        final V element = stack.get(0);
        stack.add(0, element);
    }

    @Override
    public void swap(final int n)
    {
        Preconditions.checkArgument(n >= 2, "illegal argument to swap() (" +
            n + "), must be 2 or greater");
        /*
         * As for .push(n, value), we need to check for n - 1 here
         */
        checkSize(n - 1);
        Collections.reverse(stack.subList(0, n));
    }

    @Override
    public void swap()
    {
        try {
            swap(2);
        } catch (IllegalStateException e) {
            throw new GrammarException(e.getMessage());
        }
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

    private void checkSize(final int index)
    {
        Preconditions.checkState(index < stack.size(),
            "not enough elements in stack");
    }
}
