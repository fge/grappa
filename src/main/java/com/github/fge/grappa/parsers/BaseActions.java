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

package com.github.fge.grappa.parsers;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.exceptions.InvalidGrammarException;
import com.github.fge.grappa.rules.Action;
import com.github.fge.grappa.run.context.Context;
import com.github.fge.grappa.run.context.ContextAware;
import com.github.fge.grappa.stack.ValueStack;
import com.github.fge.grappa.support.IndexRange;
import com.github.fge.grappa.support.Position;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * The basic class for all {@link ContextAware} implementations.
 *
 * <p>This class is used by all basic parsers which you may extend ({@link
 * BaseParser}, {@link EventBusParser}), but also by all of a parser's {@link
 * Action actions} (that is, any boolean expressions in rules, or any methods in
 * a parser returning a boolean).</p>
 *
 * @param <V> parameter type of the values on the parser stack
 */
public abstract class BaseActions<V>
    implements ContextAware<V>
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private Context<V> context;

    /**
     * The current context for use with action methods. Updated immediately
     * before action calls.
     *
     * @return the current context
     */
    public final Context<V> getContext()
    {
        return context;
    }

    /**
     * ContextAware interface implementation.
     *
     * @param context the context
     */
    @Override
    public final void setContext(final Context<V> context)
    {
        this.context = Objects.requireNonNull(context, "context");
    }

    /**
     * Returns the current index in the input buffer.
     *
     * @return the current index
     */
    public final int currentIndex()
    {
        check();
        return context.getCurrentIndex();
    }

    /**
     * Return the input text matched by the immediately preceding rule
     *
     * <p>This call can only be used in actions (or any implementation of {@link
     * ContextAware} that are part of a sequence rule and are not at first
     * position in this sequence.</p>
     *
     * @return the input text matched by the immediately previous rule
     */
    public String match()
    {
        check();
        return context.getMatch();
    }

    /**
     * Returns the range covering the input text matched by the immediately
     * preceding rule
     *
     * <p>This call can only be used in actions (or any implementation of {@link
     * ContextAware} that are part of a sequence rule and are not at first
     * position in this sequence.</p>
     *
     * @return a new IndexRange instance
     */
    public IndexRange matchRange()
    {
        check();
        return context.getMatchRange();
    }

    /**
     * Returns the start index of the immediately preceding rule's context
     *
     * <p>This call can only be used in actions (or any implementation of {@link
     * ContextAware} that are part of a sequence rule and are not at first
     * position in this sequence.</p>
     *
     * @return see description
     */
    public int matchStart()
    {
        check();
        return context.getMatchStartIndex();
    }

    /**
     * Returns the end index of the immediately preceding rule's context
     *
     * <p>Note that the index is exclusive; that is, it will point to the next
     * character in the buffer (possibly past the end of the sequence if the
     * previous rule matched until the end of the input).</p>
     *
     * <p>This call can only be used in actions (or any implementation of {@link
     * ContextAware} that are part of a sequence rule and are not at first
     * position in this sequence.</p>
     *
     * @return see description
     */
    public int matchEnd()
    {
        check();
        return context.getMatchEndIndex();
    }

    /**
     * Returns the current position in the underlying {@link InputBuffer} as a
     * {@link Position}
     *
     * @return the current position in the underlying inputbuffer
     */
    public Position position()
    {
        check();
        return context.getPosition();
    }

    /**
     * Pushes the given value onto the value stack
     *
     * <p>Equivalent to {@code push(0, value)}.
     *
     * @param value the value to push
     * @return true
     */
    public boolean push(final V value)
    {
        check();
        context.getValueStack().push(value);
        return true;
    }

    /**
     * Inserts the given value a given number of elements below the current top
     * of the value stack
     *
     * @param down the number of elements to skip before inserting the value (0
     * being equivalent to {@code push(value)})
     * @param value the value
     * @return true
     *
     * @throws IllegalArgumentException the stack does not contain enough
     * elements to perform this operation
     */
    public boolean push(final int down, final V value)
    {
        check();
        context.getValueStack().push(down, value);
        return true;
    }

    /**
     * Removes the value at the top of the value stack and returns it
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException the stack is empty
     */
    public V pop()
    {
        check();
        return context.getValueStack().pop();
    }

    /**
     * Removes the value at the top of the value stack and casts it before
     * returning it
     *
     * @param c the class to cast to
     * @param <E> type of the class
     * @return the current top value
     * @throws IllegalArgumentException the stack is empty
     *
     * @see #pop()
     */
    public <E extends V> E popAs(@Nonnull final Class<E> c)
    {
        return c.cast(pop());
    }

    /**
     * Removes the value the given number of elements below the top of the value
     * stack and returns it
     *
     * @param down the number of elements to skip before removing the value (0
     * being equivalent to {@code pop()})
     * @return the value
     *
     * @throws IllegalArgumentException the stack does not contain enough
     * elements to perform this operation
     */
    public V pop(final int down)
    {
        check();
        return context.getValueStack().pop(down);
    }

    /**
     * Removes the value the given number of elements below the top of the value
     * stack and casts it before returning it
     *
     * @param c the class to cast to
     * @param down the number of elements to skip before removing the value (0
     * being equivalent to {@code pop()})
     * @param <E> type of the class
     * @return the value
     * @throws IllegalArgumentException the stack does not contain enough
     * elements to perform this operation
     *
     * @see #pop(int)
     */
    public <E extends V> E popAs(final Class<E> c, final int down)
    {
        return c.cast(pop(down));
    }

    /**
     * Removes the value at the top of the value stack
     *
     * @return true
     *
     * @throws IllegalArgumentException the stack is empty
     */
    public boolean drop()
    {
        check();
        context.getValueStack().pop();
        return true;
    }

    /**
     * Removes the value the given number of elements below the top of the value
     * stack
     *
     * @param down the number of elements to skip before removing the value (0
     * being equivalent to {@code drop()})
     * @return true
     *
     * @throws IllegalArgumentException the stack does not contain enough
     * elements to perform this operation
     */
    public boolean drop(final int down)
    {
        check();
        context.getValueStack().pop(down);
        return true;
    }

    /**
     * Returns the value at the top of the value stack without removing it
     *
     * @return the current top value
     *
     * @throws IllegalArgumentException if the stack is empty
     */
    public V peek()
    {
        check();
        return context.getValueStack().peek();
    }

    /**
     * Returns and casts the value at the top of the value stack without
     * removing it
     *
     * @param c the class to cast to
     * @param <E> type of the class
     * @return the value
     *
     * @see #peek()
     */
    public <E extends V> E peekAs(final Class<E> c)
    {
        return c.cast(peek());
    }

    /**
     * Returns the value the given number of elements below the top of the value
     * stack without removing it
     *
     * @param down the number of elements to skip (0 being equivalent to {@code
     * peek()})
     * @return the value
     *
     * @throws IllegalArgumentException the stack does not contain enough
     * elements to perform this operation
     */
    public V peek(final int down)
    {
        check();
        return context.getValueStack().peek(down);
    }

    /**
     * Returns and casts the value the given number of elements below the top
     * of the value stack without removing it.
     *
     * @param c the class to cast to
     * @param down the number of elements to skip (0 being equivalent to {@code
     * peek()})
     * @param <E> type of the class
     * @return the value
     *
     * @see #peek(int)
     */
    public <E extends V> E peekAs(final Class<E> c, final int down)
    {
        return c.cast(peek(down));
    }

    /**
     * Replaces the current top value of the value stack with the given value
     *
     * <p>Equivalent to {@code poke(0, value)}.
     *
     * @param value the value
     * @return true
     *
     * @throws IllegalArgumentException the stack is empty
     */
    public boolean poke(final V value)
    {
        check();
        context.getValueStack().poke(value);
        return true;
    }

    /**
     * Replaces the element the given number of elements below the current top
     * of the value stack
     *
     * @param down the number of elements to skip before replacing the value (0
     * being equivalent to {@code poke(value)})
     * @param value the value to replace with
     * @return true
     *
     * @throws IllegalArgumentException the stack does not contain enough
     * elements to perform this operation
     */
    public boolean poke(final int down, final V value)
    {
        check();
        context.getValueStack().poke(down, value);
        return true;
    }

    /**
     * Duplicates the top value of the value stack
     *
     * @return true
     *
     * @throws IllegalArgumentException the stack is empty
     */
    public boolean dup()
    {
        check();
        context.getValueStack().dup();
        return true;
    }

    /**
     * Swaps the top two elements of the value stack.
     *
     * @return true
     *
     * @throws IllegalArgumentException the stack does not contain at least
     * two elements
     */
    public boolean swap()
    {
        check();
        context.getValueStack().swap();
        return true;
    }

    /**
     * Reverse the order of the top n elements of this context's value stack
     *
     * @param n the number of elements to swap
     * @return always true
     * @throws IllegalArgumentException stack does not contain enough elements
     *
     * @see ValueStack#swap(int)
     */
    public boolean swap(final int n)
    {
        check();
        context.getValueStack().swap(n);
        return true;
    }

    /**
     * Check whether the end of input has been reached by the current context
     *
     * @return true if the end of the input has been reached
     */
    public boolean atEnd()
    {
        check();
        return context.atEnd();
    }

    /**
     * Returns the next input character about to be matched
     *
     * <p>If you use this method, you MUST first check whether you have reached
     * the end of the buffer using {@link #atEnd()}.</p>
     *
     * @return the next input character about to be matched
     */
    public Character currentChar()
    {
        check();
        return context.getCurrentChar();
    }

    /**
     * Check whether the current context is within a predicate ({@code test()}
     * or {@code testNot()})
     *
     * <p>Useful for example for making sure actions are not run inside of a
     * predicate evaluation:</p>
     *
     * <pre>
     *     return sequence(inPredicate() || someActionHere());
     * </pre>
     *
     * @return true if in a predicate
     */
    public boolean inPredicate()
    {
        check();
        return context.inPredicate();
    }

    /**
     * Check whether the current context has recorded a parse error
     *
     * @return true if either the current rule or a sub rule has recorded a
     * parse error
     */
    public boolean hasError()
    {
        check();
        return context.hasError();
    }

    // TODO: pain point here
    private void check()
    {
        if (context == null || context.getMatcher() == null)
            throw new InvalidGrammarException("rule has an unwrapped action"
                + " expression");
    }
}
