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
import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.github.parboiled1.grappa.annotations.WillBeRemoved;

import java.util.List;

/**
 * An {@link ImmutableGraphNode} specialization representing a tree node with a parent field linking back to the nodes
 * (only) parent.
 *
 * @param <T> the actual implementation type of this ImmutableTreeNode
 */
// TODO: rename; this class IS NOT immutable
public class ImmutableTreeNode<T extends TreeNode<T>>
    extends ImmutableGraphNode<T>
    implements TreeNode<T>
{

    // we cannot make the parent field final since otherwise we can't create a tree hierarchy with parents linking to
    // their children and vice versa. So we design this for a bottom up tree construction strategy were children
    // are created first and then "acquired" by their parents
    private T parent;

    @Deprecated
    @Unused
    @WillBeRemoved(version = "1.1")
    public ImmutableTreeNode()
    {
    }

    public ImmutableTreeNode(final List<T> children)
    {
        super(children);
        // TODO: fix that! This absolutely sucks
        acquireChildren();
    }

    @Override
    @WillBeFinal(version = "1.1")
    public T getParent()
    {
        return parent;
    }

    @WillBeFinal(version = "1.1")
    // TODO: make generic! Looks like a chore...
    protected void acquireChildren()
    {
        final List<T> children = getChildren();
        for (final T child : children)
            ((ImmutableTreeNode) child).parent = this;
    }
}
