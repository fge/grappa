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

import com.github.parboiled1.grappa.cleanup.DoNotUse;
import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.github.parboiled1.grappa.cleanup.WillBePrivate;
import com.github.parboiled1.grappa.cleanup.WillBeRemoved;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An implementation of a stack of value objects providing an efficient snapshot capability and a number of convenience
 * methods. The current state of the stack can be saved and restored in small constant time with the methods
 * {@link #takeSnapshot()} and {@link #restoreSnapshot(Object)} ()}. The implementation also serves as an Iterable
 * over the current stack values (the values are being provided with the last value (on top of the stack) first).
 *
 * @param <V> the type of the value objects
 *
 * @deprecated use {@link com.github.parboiled1.grappa.stack.DefaultValueStack}
 * instead
 */
@Deprecated
@WillBeRemoved(version = "1.1")
public class DefaultValueStack<V>
    implements ValueStack<V>
{
    //TODO: replace with a ListHead
    @DoNotUse
    @WillBeFinal(version = "1.1")
    @WillBePrivate(version = "1.1")
    protected static class Element
    {
        protected final Object value;
        protected final Element tail;

        protected Element(final Object value, final Element tail)
        {
            this.value = value;
            this.tail = tail;
        }
    }

    protected Element head;
    protected V tempValue;

    /**
     * Initializes an empty value stack.
     */
    public DefaultValueStack()
    {
    }

    /**
     * Initializes a value stack containing the given values with the last value
     * being at the top of the stack.
     *
     * @param values the initial stack values
     */
    public DefaultValueStack(final Iterable<V> values)
    {
        pushAll(values);
    }

    @Override
    @WillBeFinal(version = "1.1")
    public boolean isEmpty()
    {
        return head == null;
    }

    @Override
    @WillBeFinal(version = "1.1")
    public int size()
    {
        Element cursor = head;
        int size = 0;
        while (cursor != null) {
            size++;
            cursor = cursor.tail;
        }
        return size;
    }

    @Override
    public void clear()
    {
        head = null;
    }

    @Override
    @WillBeFinal(version = "1.1")
    public Object takeSnapshot()
    {
        return head;
    }

    @Override
    public void restoreSnapshot(final Object snapshot)
    {
        try {
            head = (Element) snapshot;
        } catch (ClassCastException ignored) {
            throw new IllegalArgumentException("Given argument '" + snapshot
                + "' is not a valid snapshot element");
        }
    }

    @Override
    public void push(final V value)
    {
        head = new Element(value, head);
    }

    @Override
    public void push(final int down, final V value)
    {
        head = push(down, value, head);
    }

    private static Element push(final int down, final Object value,
        final Element head)
    {
        if (down == 0)
            return new Element(value, head);
        Preconditions.checkArgument(head != null,
            "Cannot push beyond the bottom of the stack");
        if (down > 0)
            return new Element(head.value, push(down - 1, value, head.tail));
        throw new IllegalArgumentException(
            "Argument 'down' must not be negative");
    }

    @Override
    @WillBeFinal(version = "1.1")
    public void pushAll(final V firstValue, final V... moreValues)
    {
        push(firstValue);
        for (final V value : moreValues)
            push(value);
    }

    @Override
    @WillBeFinal(version = "1.1")
    public void pushAll(@Nonnull final Iterable<V> values)
    {
        head = null;
        for (final V value : values)
            push(value);
    }

    @Override
    @WillBeFinal(version = "1.1")
    public V pop()
    {
        return pop(0);
    }

    @Override
    public V pop(final int down)
    {
        head = pop(down, head);
        final V result = tempValue;
        tempValue = null; // avoid memory leak
        return result;
    }

    @SuppressWarnings("unchecked")
    private Element pop(final int down, final Element head)
    {
        Preconditions.checkNotNull(head,
            "Cannot pop from beyond the bottom of the stack");
        if (down == 0) {
            tempValue = (V) head.value;
            return head.tail;
        }
        if (down > 0)
            return new Element(head.value, pop(down - 1, head.tail));
        throw new IllegalArgumentException("Argument 'down' must not be " +
            "negative");
    }

    @Override
    @WillBeFinal(version = "1.1")
    public V peek()
    {
        return peek(0);
    }

    @Override
    @WillBeFinal(version = "1.1")
    @SuppressWarnings("unchecked")
    public V peek(final int down)
    {
        return (V) peek(down, head);
    }

    private static Object peek(final int down, final Element head)
    {
        Preconditions.checkNotNull(head,
            "Cannot peek beyond the bottom of the stack");
        if (down == 0)
            return head.value;
        if (down > 0)
            return peek(down - 1, head.tail);
        throw new IllegalArgumentException("Argument 'down' must not be" +
            " negative");
    }

    @Override
    @WillBeFinal(version = "1.1")
    public void poke(final V value)
    {
        poke(0, value);
    }

    @Override
    public void poke(final int down, final V value)
    {
        head = poke(down, value, head);
    }

    private static Element poke(final int down, final Object value,
        final Element head)
    {
        Preconditions.checkArgument(head != null,
            "Cannot poke beyond the bottom of the stack");
        if (down == 0)
            return new Element(value, head.tail);
        if (down > 0)
            return new Element(head.value, poke(down - 1, value, head.tail));
        throw new IllegalArgumentException(
            "Argument 'down' must not be negative");
    }

    @Override
    @WillBeFinal(version = "1.1")
    public void dup()
    {
        push(peek());
    }

    @Override
    public void swap()
    {
        Checks.ensure(isSizeGTE(2, head),
            "Swap not allowed on stack with less than two elements");
        final Element down1 = head.tail;
        head = new Element(down1.value, new Element(head.value, down1.tail));
    }

    @Override
    public void swap3()
    {
        Checks.ensure(isSizeGTE(3, head),
            "Swap3 not allowed on stack with less than 3 elements");
        final Element down1 = head.tail;
        final Element down2 = down1.tail;
        head = new Element(down2.value,
            new Element(down1.value, new Element(head.value, down2.tail)));
    }

    @Override
    public void swap4()
    {
        Checks.ensure(isSizeGTE(4, head),
            "Swap4 not allowed on stack with less than 4 elements");
        final Element down1 = head.tail;
        final Element down2 = down1.tail;
        final Element down3 = down2.tail;
        head = new Element(down3.value, new Element(down2.value,
            new Element(down1.value, new Element(head.value, down3.tail))));
    }

    @Override
    public void swap5()
    {
        Checks.ensure(isSizeGTE(5, head),
            "Swap5 not allowed on stack with less than 5 elements");
        final Element down1 = head.tail;
        final Element down2 = down1.tail;
        final Element down3 = down2.tail;
        final Element down4 = down3.tail;
        head = new Element(down4.value, new Element(down3.value,
            new Element(down2.value, new Element(down1.value,
                new Element(head.value, down4.tail)))));
    }

    @Override
    public void swap6()
    {
        Checks.ensure(isSizeGTE(6, head),
            "Swap6 not allowed on stack with less than 6 elements");
        final Element down1 = head.tail;
        final Element down2 = down1.tail;
        final Element down3 = down2.tail;
        final Element down4 = down3.tail;
        final Element down5 = down4.tail;
        head = new Element(down5.value, new Element(down4.value,
            new Element(down3.value, new Element(down2.value,
                new Element(down1.value,
                    new Element(head.value, down5.tail))))));
    }

    private static boolean isSizeGTE(final int minSize, final Element head)
    {
        return minSize == 1 ? head != null : isSizeGTE(minSize - 1, head.tail);
    }

    @Override
    @WillBeFinal(version = "1.1")
    public Iterator<V> iterator()
    {
        return new Iterator<V>()
        {
            private Element next = head;

            @Override
            public boolean hasNext()
            {
                return next != null;
            }

            @Override
            @SuppressWarnings("unchecked")
            public V next()
            {
                final V value = (V) next.value;
                next = next.tail;
                return value;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
