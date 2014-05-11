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
import org.parboiled.support.ValueStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class DefaultValueStack<V>
    implements ValueStack<V>
{
    private List<V> stack = new ArrayList<V>();

    /**
     * Determines whether the stack is empty.
     *
     * @return true if empty
     */
    @Override
    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    /**
     * Returns the number of elements currently on the stack.
     *
     * @return the number of elements
     */
    @Override
    public int size()
    {
        return stack.size();
    }

    /**
     * Clears all values.
     */
    @Override
    public void clear()
    {
        stack.clear();
    }

    /**
     * Returns an object representing the current state of the stack.
     *
     * @return an object representing the current state of the stack
     */
    @Override
    public Object takeSnapshot()
    {
        return new ArrayList<V>(stack);
    }

    /**
     * Restores the stack state as previously returned by {@link
     * #takeSnapshot()}.
     *
     * @param snapshot a snapshot object previously returned by {@link
     * #takeSnapshot()}
     */
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

    /**
     * Pushes the given value onto the stack. Equivalent to push(0, value).
     *
     * @param value the value
     */
    @Override
    public void push(final V value)
    {
        push(0, value);
    }

    /**
     * Inserts the given value a given number of elements below the current top
     * of the stack.
     *
     * @param down the number of elements to skip before inserting the value (0
     * being equivalent to push(value))
     * @param value the value
     * @throws IllegalArgumentException if the stack does not contain enough
     * elements to perform this operation
     */
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

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *
     * @param firstValue the first value
     * @param moreValues the other values
     */
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

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *
     * @param values the values
     */
    @Override
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        final List<V> newStack = Lists.newArrayList(values);
        newStack.addAll(stack);
        stack = newStack;
    }

    /**
     * Removes the value at the top of the stack and returns it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    public V pop()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        return stack.remove(0);
    }

    /**
     * Removes the value the given number of elements below the top of the
     * stack.
     *
     * @param down the number of elements to skip before removing the value (0
     * being equivalent to pop())
     * @return the value
     *
     * @throws IllegalArgumentException if the stack does not contain enough
     * elements to perform this operation
     */
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

    /**
     * Returns the value at the top of the stack without removing it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    public V peek()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        return stack.get(0);
    }

    /**
     * Returns the value the given number of elements below the top of the stack
     * without removing it.
     *
     * @param down the number of elements to skip (0 being equivalent to peek())
     * @return the value
     *
     * @throws IllegalArgumentException if the stack does not contain enough
     * elements to perform this operation
     */
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

    /**
     * Replaces the current top value with the given value. Equivalent to
     * poke(0, value).
     *
     * @param value the value
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    public void poke(@Nullable final V value)
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        poke(0, value);
    }

    /**
     * Replaces the element the given number of elements below the current top
     * of the stack.
     *
     * @param down the number of elements to skip before replacing the value (0
     * being equivalent to poke(value))
     * @param value the value to replace with
     * @throws IllegalArgumentException if the stack does not contain enough
     * elements to perform this operation
     */
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

    /**
     * Duplicates the top value. Equivalent to push(peek()).
     *
     * @throws IllegalArgumentException if the stack is empty
     */
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
        /*
         * As for .push(n, value), we need to check for n - 1 here
         */
        checkSize(n - 1);
        Collections.reverse(stack.subList(0, n));
    }

    /**
     * Swaps the top two stack values.
     *
     * @throws GrammarException if the stack does not contain at least two
     * elements
     */
    @Override
    public void swap()
    {
        try {
            swap(2);
        } catch (IllegalStateException e) {
            throw new GrammarException(e.getMessage());
        }
    }

    /**
     * Reverses the order of the top 3 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 3
     * elements
     */
    @Override
    public void swap3()
    {
        try {
            swap(3);
        } catch (IllegalStateException e) {
            throw new GrammarException(e.getMessage());
        }
    }

    /**
     * Reverses the order of the top 4 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 4
     * elements
     */
    @Override
    public void swap4()
    {
        try {
            swap(4);
        } catch (IllegalStateException e) {
            throw new GrammarException(e.getMessage());
        }
    }

    /**
     * Reverses the order of the top 5 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 5
     * elements
     */
    @Override
    public void swap5()
    {
        try {
            swap(5);
        } catch (IllegalStateException e) {
            throw new GrammarException(e.getMessage());
        }
    }

    /**
     * Reverses the order of the top 6 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 6
     * elements
     */
    @Override
    public void swap6()
    {
        try {
            swap(6);
        } catch (IllegalStateException e) {
            throw new GrammarException(e.getMessage());
        }
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
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
