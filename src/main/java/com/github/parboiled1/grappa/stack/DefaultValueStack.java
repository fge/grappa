package com.github.parboiled1.grappa.stack;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.parboiled.errors.GrammarException;
import org.parboiled.support.ValueStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class DefaultValueStack<V>
    implements ValueStack<V>
{
    private LinkedList<V> stack = new LinkedList<V>();

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
     * This cost of running this operation is negligible and independent from
     * the size of the stack.
     *
     * @return an object representing the current state of the stack
     */
    @Nonnull
    @Override
    public Object takeSnapshot()
    {
        return new LinkedList<V>(stack);
    }

    /**
     * Restores the stack state as previously returned by {@link
     * #takeSnapshot()}. This cost of running this operation is negligible and
     * independent from the size of the stack.
     *
     * @param snapshot a snapshot object previously returned by {@link
     * #takeSnapshot()}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void restoreSnapshot(@Nonnull final Object snapshot)
    {
        Preconditions.checkState(snapshot.getClass() == LinkedList.class);
        stack = (LinkedList<V>) snapshot;
    }

    /**
     * Pushes the given value onto the stack. Equivalent to push(0, value).
     *
     * @param value the value
     */
    @Override
    public void push(@Nonnull final V value)
    {
        Preconditions.checkNotNull(value, "null elements are not allowed");
        stack.push(value);
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
    public void push(final int down, @Nonnull final V value)
    {
        Preconditions.checkNotNull(value, "null elements are not allowed");
        /*
         * It is legal to append at the end! We must therefore check that the
         * index - 1 is strictly less than size, not the index itself
         */
        checkSize(down - 1);
        stack.add(down, value);
    }

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *
     * @param firstValue the first value
     * @param moreValues the other values
     */
    @Override
    @SuppressWarnings("unchecked")
    public void pushAll(@Nonnull final V firstValue,
        @Nonnull final V... moreValues)
    {
        Preconditions.checkNotNull(firstValue, "null elements are not allowed");
        Preconditions.checkNotNull(moreValues, "null elements are not allowed");

        for (final V value: moreValues)
            Preconditions.checkNotNull(value, "null elements are not allowed");

        // FIXME: hackish :/ Can throw ClassCastException all right
        if (firstValue instanceof Iterable && moreValues.length == 0) {
            pushAll((Iterable<V>) firstValue);
            return;
        }
        final List<V> list = ImmutableList.<V>builder()
            .add(firstValue).add(moreValues).build();
        pushAll(list);
    }

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *
     * @param values the values
     */
    @Override
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        for (final V value: values)
            Preconditions.checkNotNull(value, "null elements are not allowed");
        final LinkedList<V> newStack = Lists.newLinkedList(values);
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
    @Nonnull
    @Override
    public V pop()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        return stack.pop();
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
    @Nonnull
    @Override
    public V pop(final int down)
    {
        checkSize(down);
        return stack.remove(down);
    }

    /**
     * Returns the value at the top of the stack without removing it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Nonnull
    @Override
    public V peek()
    {
        Preconditions.checkArgument(!stack.isEmpty(), "stack is empty");
        return stack.peek();
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
    @Nonnull
    @Override
    public V peek(final int down)
    {
        checkSize(down);
        return stack.get(down);
    }

    /**
     * Replaces the current top value with the given value. Equivalent to
     * poke(0, value).
     *
     * @param value the value
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    public void poke(@Nonnull final V value)
    {
        Preconditions.checkNotNull(value, "null elements are not allowed");
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
    public void poke(final int down, @Nonnull final V value)
    {
        checkSize(down);
        stack.set(down, value);
    }

    /**
     * Duplicates the top value. Equivalent to push(peek()).
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    public void dup()
    {
        stack.push(peek());
    }

    // TODO: make this part of the interface
    @VisibleForTesting
    void swap(final int n)
    {
        /*
         * As for .push(n, value), we need to check for n - 1 here
         */
        // TODO: hack! See interface description
        try {
            checkSize(n - 1);
        } catch (IllegalArgumentException e) {
            throw new GrammarException(e.getMessage());
        }
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
        swap(2);
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
        swap(3);
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
        swap(4);
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
        swap(5);
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
        swap(6);
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
        Preconditions.checkArgument(index < stack.size(),
            "not enough elements in stack");
    }
}
