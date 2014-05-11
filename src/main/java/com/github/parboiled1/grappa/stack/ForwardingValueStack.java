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

import com.github.parboiled1.grappa.cleanup.ThrownExceptionsWillChange;
import org.parboiled.errors.GrammarException;
import org.parboiled.support.ValueStack;

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

    /**
     * Determines whether the stack is empty.
     *
     * @return true if empty
     */
    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    /**
     * Returns the number of elements currently on the stack.
     *
     * @return the number of elements
     */
    @Override
    public int size()
    {
        return delegate.size();
    }

    /**
     * Clears all values.
     */
    @Override
    public void clear()
    {
        delegate.clear();
    }

    /**
     * Returns an object representing the current state of the stack.
     *
     * @return an object representing the current state of the stack
     */
    @Override
    @Nullable
    public Object takeSnapshot()
    {
        return delegate.takeSnapshot();
    }

    /**
     * Restores the stack state as previously returned by {@link
     * #takeSnapshot()}.
     *
     * @param snapshot a snapshot object previously returned by {@link
     * #takeSnapshot()}
     */
    @Override
    public void restoreSnapshot(@Nullable final Object snapshot)
    {
        delegate.restoreSnapshot(snapshot);
    }

    /**
     * Pushes the given value onto the stack. Equivalent to push(0, value).
     *
     * @param value the value
     */
    public void push(@Nullable final V value)
    {
        delegate.push(value);
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
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void push(final int down, @Nullable final V value)
    {
        delegate.push(down, value);
    }

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *  @param firstValue the first value
     * @param moreValues the other values
     */
    public void pushAll(@Nullable final V firstValue,
        @Nullable final V... moreValues)
    {
        delegate.pushAll(firstValue, moreValues);
    }

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *
     * @param values the values
     */
    @Deprecated
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        delegate.pushAll(values);
    }

    /**
     * Removes the value at the top of the stack and returns it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    @Nullable
    public V pop()
    {
        return delegate.pop();
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
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    @Nullable
    public V pop(final int down)
    {
        return delegate.pop(down);
    }

    /**
     * Returns the value at the top of the stack without removing it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    @Nullable
    public V peek()
    {
        return delegate.peek();
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
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    @Nullable
    public V peek(final int down)
    {
        return delegate.peek(down);
    }

    /**
     * Replaces the current top value with the given value. Equivalent to
     * poke(0, value).
     *
     * @param value the value
     * @throws IllegalArgumentException if the stack is empty
     */
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void poke(@Nullable final V value)
    {
        delegate.poke(value);
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
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void poke(final int down, @Nullable final V value)
    {
        delegate.poke(down, value);
    }

    /**
     * Duplicates the top value. Equivalent to push(peek()).
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void dup()
    {
        delegate.dup();
    }

    /**
     * Reverses the order of the top n stack values
     *
     * @param n the number of elements to reverse
     * @throws IllegalArgumentException {@code n} is less than 2
     * @throws IllegalStateException the stack does not contain at least n
     * elements
     */
    @Override
    public void swap(final int n)
    {
        delegate.swap(n);
    }

    /**
     * Swaps the top two stack values.
     *
     * @throws GrammarException if the stack does not contain at least two
     * elements
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void swap()
    {
        delegate.swap();
    }

    /**
     * Reverses the order of the top 3 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 3
     * elements
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void swap3()
    {
        delegate.swap3();
    }

    /**
     * Reverses the order of the top 4 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 4
     * elements
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void swap4()
    {
        delegate.swap4();
    }

    /**
     * Reverses the order of the top 5 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 5
     * elements
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void swap5()
    {
        delegate.swap5();
    }

    /**
     * Reverses the order of the top 6 stack values.
     *
     * @throws GrammarException if the stack does not contain at least 6
     * elements
     */
    @Override
    @ThrownExceptionsWillChange(version = "1.1",
        to = IllegalStateException.class)
    public void swap6()
    {
        delegate.swap6();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<V> iterator()
    {
        return delegate.iterator();
    }
}
