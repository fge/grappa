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

package org.parboiled.trees;

import com.github.parboiled1.grappa.annotations.Unused;
import com.github.parboiled1.grappa.annotations.WillBeRemoved;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A base implementation of the {@link MutableTreeNode}.
 *
 * @param <T> the actual implementation type of this MutableTreeNodeImpl
 */
@Deprecated
@Unused
@WillBeRemoved(version = "1.1")
public class MutableTreeNodeImpl<T extends MutableTreeNode<T>>
    implements MutableTreeNode<T>
{

    private final List<T> children = new ArrayList<T>();
    // TODO: the fact that this exists absolutely sucks
    private final List<T> childrenView = Collections.unmodifiableList(children);

    @Nullable
    // TODO: null! again!
    private T parent;

    @Override
    public final T getParent()
    {
        return parent;
    }

    @Override
    public final List<T> getChildren()
    {
        return childrenView;
    }

    @Override
    public void addChild(final int index, final T child)
    {
        Preconditions.checkElementIndex(index, children.size() + 1);

        // detach new child from old parent
        if (child != null) {
            if (child.getParent() == this)
                return;
            if (child.getParent() != null) {
                TreeUtils.removeChild(child.getParent(), child);
            }
        }

        // attach new child
        children.add(index, child);
        setParent(child, this);
    }

    @Override
    public final void setChild(final int index, final T child)
    {
        Preconditions.checkElementIndex(index, children.size());

        // detach old child
        final T old = children.get(index);
        if (old == child)
            return;
        setParent(old, null);

        // detach new child from old parent
        if (child != null && child.getParent() != this) {
            TreeUtils.removeChild(child.getParent(), child);
        }

        // attach new child
        children.set(index, child);
        setParent(child, this);
    }

    @Override
    public T removeChild(final int index)
    {
        Preconditions.checkElementIndex(index, children.size());
        final T removed = children.remove(index);
        setParent(removed, null);
        return removed;
    }

    private static <T extends MutableTreeNode<T>> void setParent(final T node,
        final MutableTreeNodeImpl<T> parent)
    {
        if (node != null)
            ((MutableTreeNodeImpl) node).parent = parent;
    }
}