/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Heavy modifications by Francis Galiegue <fgaliegue@gmail.com>
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

import com.github.parboiled1.grappa.annotations.DoNotUse;
import com.github.parboiled1.grappa.annotations.Unused;
import com.github.parboiled1.grappa.annotations.WillBeRemoved;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

// TODO: get rid of it
@Deprecated
@Unused
@DoNotUse
@WillBeRemoved(version = "1.1")
public final class ImmutableLinkedList<T>
    extends ForwardingList<T>
{
    private final List<T> elements;

    @Override
    protected List<T> delegate()
    {
        return elements;
    }

    public static <T> ImmutableLinkedList<T> nil()
    {
        return new ImmutableLinkedList<T>(ImmutableList.<T>of());
    }

    private ImmutableLinkedList(final T head, final ImmutableLinkedList<T> tail)
    {
        elements = ImmutableList.<T>builder().add(head).addAll(tail).build();
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

    /**
     * UNUSED!
     *
     * @return a list with all elements reversed
     *
     * @deprecated Unused! Will be removed in 1.1
     */
    @Deprecated
    public ImmutableLinkedList<T> reverse()
    {
        return new ImmutableLinkedList<T>(Lists.reverse(elements));
    }
}
