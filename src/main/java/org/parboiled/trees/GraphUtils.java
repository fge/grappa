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

import com.github.parboiled1.grappa.annotations.DoNotUse;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.parboiled.common.Formatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * General utility methods for operating on directed graphs (consisting of
 * {@link GraphNode}s).
 */
final class GraphUtils
{
    private GraphUtils()
    {
    }

    /**
     * Creates a string representation of the graph reachable from the given
     * node using the given formatter.
     *
     * @param node the root node
     * @param formatter the node formatter
     * @return a new string
     */
    @VisibleForTesting
    @DoNotUse
    static <T extends GraphNode<T>> String printTree(final T node,
        final Formatter<T> formatter)
    {
        Preconditions.checkNotNull(formatter, "formatter");
        return printTree(node, formatter, Predicates.<T>alwaysTrue(),
            Predicates.<T>alwaysTrue());
    }

    /**
     * Creates a string representation of the graph reachable from the given
     * node using the given formatter. The given predicate determines whether a
     * particular node (and its subtree respectively) is to be printed or not.
     *
     * @param node the root node
     * @param formatter the node formatter
     * @param nodeFilter the predicate selecting the nodes to print
     * @param subTreeFilter the predicate determining whether to descend into a
     * given nodes subtree or not
     * @return a new string
     */
    @Nonnull
    // TODO: null! again!
    static <T extends GraphNode<T>> String printTree(
        @Nullable final T node, @Nonnull final Formatter<T> formatter,
        @Nonnull final Predicate<T> nodeFilter,
        @Nonnull final Predicate<T> subTreeFilter)
    {
        Preconditions.checkNotNull(formatter, "formatter");
        Preconditions.checkNotNull(nodeFilter, "nodeFilter");
        Preconditions.checkNotNull(subTreeFilter, "subTreeFilter");
        if (node == null)
            return "";
        return printTree(node, formatter, "",
            new StringBuilder(), nodeFilter, subTreeFilter).toString();
    }

    // private recursion helper

    private static <T extends GraphNode<T>> StringBuilder printTree(
        final T node, final Formatter<T> formatter, String indent,
        final StringBuilder sb, final Predicate<T> nodeFilter,
        final Predicate<T> subTreeFilter)
    {
        if (nodeFilter.apply(node)) {
            final String line = formatter.format(node);
            if (line != null) {
                sb.append(indent).append(line).append('\n');
                indent += "  ";
            }
        }
        if (!subTreeFilter.apply(node))
            return sb;

        for (final T sub : node.getChildren())
            printTree(sub, formatter, indent, sb, nodeFilter, subTreeFilter);

        return sb;
    }
}
