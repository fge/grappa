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

package org.parboiled.common;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.ListIterator;

// TODO: get rid of it
public class ImmutableLinkedList<T>
    extends ForwardingList<T>
{
    private static final ImmutableLinkedList<Object> NIL
        = new ImmutableLinkedList<Object>(ImmutableList.of())
    {
        @Override
        public Object head()
        {
            throw new UnsupportedOperationException("head of empty list");
        }

        @Override
        public ImmutableLinkedList<Object> tail()
        {
            throw new UnsupportedOperationException("tail of empty list");
        }

        @Override
        public Object last()
        {
            throw new UnsupportedOperationException("last of empty list");
        }

        @Override
        protected List<Object> delegate()
        {
            return ImmutableList.of();
        }

        @Override
        public ListIterator<Object> listIterator(final int index)
        {
            return ImmutableList.of().listIterator();
        }
    };

    private final List<T> elements;

    @Override
    protected List<T> delegate()
    {
        return elements;
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableLinkedList<T> nil()
    {
        return (ImmutableLinkedList<T>) NIL;
    }

    public ImmutableLinkedList(final T head, final ImmutableLinkedList<T> tail)
    {
        elements = ImmutableList.<T>builder().add(head)
            .addAll(tail).build();
    }

    private ImmutableLinkedList(final List<T> elements)
    {
        this.elements = ImmutableList.copyOf(elements);
    }

    public T head()
    {
        return Iterables.getFirst(elements, null);
    }

    public ImmutableLinkedList<T> tail()
    {
        return new ImmutableLinkedList<T>(elements.subList(1, elements.size()));
    }

    public T last()
    {
        return Iterables.getLast(elements);
    }

    public ImmutableLinkedList<T> prepend(final T object)
    {
        return new ImmutableLinkedList<T>(object, this);
    }

    public ImmutableLinkedList<T> reverse()
    {
        return new ImmutableLinkedList<T>(Lists.reverse(elements));
    }
}
