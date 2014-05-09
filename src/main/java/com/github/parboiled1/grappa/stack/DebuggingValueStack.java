package com.github.parboiled1.grappa.stack;

import com.github.parboiled1.grappa.misc.SystemOutCharSource;
import com.google.common.base.Joiner;
import com.google.common.io.CharSink;
import org.parboiled.errors.GrammarException;
import org.parboiled.support.ValueStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

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
        this(delegate, SystemOutCharSource.INSTANCE);
    }

    /**
     * Clears all values.
     */
    @Override
    public void clear()
    {
        log("clear");
        super.clear();
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
        log("restore snapshot: " + snapshot);
        super.restoreSnapshot(snapshot);
    }

    /**
     * Pushes the given value onto the stack. Equivalent to push(0, value).
     *
     * @param value the value
     */
    @Override
    public void push(@Nullable final V value)
    {
        log("push: " + value);
        super.push(value);
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
    public void push(final int down, @Nullable final V value)
    {
        log("push " + down + ": " + value);
        super.push(down, value);
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
    @Nullable
    @Override
    public V pop(final int down)
    {
        log("pop: " + down);
        return super.pop(down);
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
        log("poke " + down + ": " + value);
        super.poke(down, value);
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
        log("pushAll: " + JOINER.join(firstValue, moreValues));
        super.pushAll(firstValue, moreValues);
    }

    /**
     * Returns an object representing the current state of the stack.
     *
     * @return an object representing the current state of the stack
     */
    @Nullable
    @Override
    public Object takeSnapshot()
    {
        log("take snapshot");
        return super.takeSnapshot();
    }

    /**
     * Removes the value at the top of the stack and returns it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Nullable
    @Override
    public V pop()
    {
        log("pop");
        return super.pop();
    }

    /**
     * Returns the value at the top of the stack without removing it.
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Nullable
    @Override
    public V peek()
    {
        log("peek");
        return super.peek();
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
    @Nullable
    @Override
    public V peek(final int down)
    {
        log("peek " + down);
        return super.peek(down);
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
        log("poke: " + value);
        super.poke(value);
    }

    /**
     * Duplicates the top value. Equivalent to push(peek()).
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    @Override
    public void dup()
    {
        log("dup");
        super.dup();
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
        log("swap");
        super.swap();
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
        log("swap6");
        super.swap6();
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
        log("swap5");
        super.swap5();
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
        log("swap4");
        super.swap4();
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
        log("swap3");
        super.swap3();
    }

    /**
     * Pushes all given elements onto the stack (in the order as given).
     *
     * @param values the values
     */
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
