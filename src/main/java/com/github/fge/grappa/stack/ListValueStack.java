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

package com.github.fge.grappa.stack;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A {@link ValueStack} implementation using an {@link ArrayList}
 *
 * <p>UNUSED!</p>
 *
 * @param <V> type parameter of the stack's element
 */
@ParametersAreNonnullByDefault
public final class ListValueStack<V>
    extends ValueStackBase<V>
{
    private List<V> stack = new ArrayList<>();

    public ListValueStack()
    {
    }

    @VisibleForTesting
    ListValueStack(final Collection<V> collection)
    {
        stack.addAll(collection);
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
        Objects.requireNonNull(snapshot);
        Preconditions.checkState(snapshot.getClass() == ArrayList.class);
        stack = (List<V>) snapshot;
    }

    @Override
    protected void doPush(final int down, final V value)
    {
        stack.add(down, value);
    }

    @Nonnull
    @Override
    protected V doPop(final int down)
    {
        return stack.remove(down);
    }

    @Nonnull
    @Override
    protected V doPeek(final int down)
    {
        return stack.get(down);
    }

    @Override
    protected void doPoke(final int down, final V value)
    {
        stack.set(down, value);
    }

    @Override
    protected void doDup()
    {
        final V element = stack.get(0);
        stack.add(0, element);
    }

    @Override
    protected void doSwap(final int n)
    {
        Collections.reverse(stack.subList(0, n));
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
}
