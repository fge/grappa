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

import com.github.fge.grappa.buffers.InputBuffer;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import org.parboiled.Node;
import org.parboiled.support.Chars;
import org.parboiled.support.LabelPrefixPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * General utility methods for operating on parse trees.
 */
public final class ParseTreeUtils
{

    private ParseTreeUtils()
    {
    }

    /**
     * <p>Returns the parse tree node underneath the given parent that matches
     * the given path.</p>
     * <p>The path is a '/' separated list of node label prefixes describing the
     * ancestor chain of the node to look for relative to the given parent node.
     * If there are several nodes that match the given path the method returns
     * the first one unless the respective path segments has the special prefix
     * "last:". In this case the last matching node is returned.
     * <p><b>Example:</b> "per/last:so/fix" will return the first node, whose
     * label starts with "fix" under the last node, whose label starts with "so"
     * under the first node, whose label starts with "per".</p>
     * If parent is null or no node is found the method returns null.
     *
     * @param parent the parent Node
     * @param path the path to the Node being searched for
     * @return the Node if found or null if not found
     */
    @Nullable // TODO: try and get rid of that null!
    public static <V> Node<V> findNodeByPath(final Node<V> parent,
        final String path)
    {
        Objects.requireNonNull(path, "path");
        return parent != null && hasChildren(parent)
            ? findNodeByPath(parent.getChildren(), path)
            : null;
    }

    /**
     * Returns the node underneath the given parents that matches the given path.
     * See {@link #findNodeByPath(Node, String)} )} for a description of the path argument.
     * If the given collections of parents is null or empty or no node is found the method returns null.
     *
     * @param parents the parent Nodes to look through
     * @param path    the path to the Node being searched for
     * @return the Node if found or null if not found
     */
    @Nullable
    public static <V> Node<V> findNodeByPath(
        final List<Node<V>> parents, final String path) {
        Objects.requireNonNull(path, "path");
        if (parents == null)
            return null;
        if (parents.isEmpty())
            return null;
        final int separatorIndex = path.indexOf('/');
        String prefix = separatorIndex != -1
            ? path.substring(0, separatorIndex)
            : path;
        int start = 0, step = 1;
        if (prefix.startsWith("last:")) {
            prefix = prefix.substring(5);
            start = parents.size() - 1;
            step = -1;
        }
        for (int i = start; 0 <= i && i < parents.size(); i += step) {
            final Node<V> child = parents.get(i);
            // TODO! null again!
            if (Strings.nullToEmpty(child.getLabel()).startsWith(prefix)) {
                return separatorIndex == -1
                    ? child
                    : findNodeByPath(child, path.substring(separatorIndex + 1));
            }
        }
        return null;
    }

    /**
     * Collects all nodes underneath the given parent that match the given path.
     * The path is a '/' separated list of node label prefixes describing the
     * ancestor chain of the node to look for relative to the given parent node.
     *
     * @param parent     the parent Node
     * @param path       the path to the Nodes being searched for
     * @param collection the collection to collect the found Nodes into
     * @return the same collection instance passed as a parameter
     */
    public static <V, C extends Collection<Node<V>>> C collectNodesByPath(
        final Node<V> parent, final String path, final C collection) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(collection, "collection");
        return parent != null && hasChildren(parent)
            ? collectNodesByPath(parent.getChildren(), path, collection)
            : collection;
    }

    /**
     * Collects all nodes underneath the given parents that match the given path.
     * The path is a '/' separated list of node label prefixes describing the
     * ancestor chain of the node to look for relative to the given parent
     * nodes.
     *
     * @param parents    the parent Nodes to look through
     * @param path       the path to the Nodes being searched for
     * @param collection the collection to collect the found Nodes into
     * @return the same collection instance passed as a parameter
     */
    @Nonnull
    // TODO: nullable!
    public static <V, C extends Collection<Node<V>>> C collectNodesByPath(
        @Nullable final List<Node<V>> parents, @Nonnull final String path,
        @Nonnull final C collection)
    {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(collection, "collection");
        if (parents == null)
            return collection;
        if (parents.isEmpty())
            return collection;
        final int separatorIndex = path.indexOf('/');
        final String prefix = separatorIndex != -1
            ? path.substring(0, separatorIndex)
            : path;
        for (final Node<V> child: parents) {
            if (!prefix.startsWith(Strings.nullToEmpty(child.getLabel())))
                continue;

            if (separatorIndex == -1)
                collection.add(child);
            else
                collectNodesByPath(child, path.substring(separatorIndex + 1),
                    collection);

        }
        return collection;
    }

    /**
     * Returns the first node underneath the given parent for which the given
     * predicate evaluates to true. If parent is null or no node is found the
     * method returns null.
     *
     * @param parent    the parent Node
     * @param predicate the predicate
     * @return the Node if found or null if not found
     */
    @Nullable // TODO! Null again!
    public static <V> Node<V> findNode(@Nullable final Node<V> parent,
        @Nonnull final Predicate<Node<V>> predicate)
    {
        Objects.requireNonNull(predicate, "predicate");
        if (parent == null)
            return null;
        if (predicate.apply(parent))
            return parent;
        if (!hasChildren(parent))
            return null;

        return findNode(parent.getChildren(), predicate);
    }

    /**
     * Returns the first node underneath the given parents for which the given
     * predicate evaluates to true. If parents is null or empty or no node is
     * found the method returns null.
     *
     * @param parents   the parent Nodes to look through
     * @param predicate the predicate
     * @return the Node if found or null if not found
     */
    @Nullable // TODO! null again!
    public static <V> Node<V> findNode(@Nullable final List<Node<V>> parents,
        @Nonnull final Predicate<Node<V>> predicate)
    {
        Objects.requireNonNull(predicate, "predicate");
        if (parents == null)
            return null;
        if (parents.isEmpty())
            return null;
        for (final Node<V> child: parents) {
            final Node<V> found = findNode(child, predicate);
            if (found != null)
                return found;
        }
        return null;
    }

    /**
     * Returns the first node underneath the given parents which matches the
     * given label prefix. If parents is null or empty or no node is found the
     * method returns null.
     *
     * @param parents     the parent Nodes to look through
     * @param labelPrefix the label prefix to look for
     * @return the Node if found or null if not found
     */
    @Nullable // TODO! Null again!
    public static <V> Node<V> findNodeByLabel(
        @Nullable final List<Node<V>> parents,
        @Nonnull final String labelPrefix)
    {
        return findNode(parents, new LabelPrefixPredicate<V>(labelPrefix));
    }

    /**
     * Returns the last node underneath the given parent for which the given
     * predicate evaluates to true. If parent is null or no node is found the
     * method returns null.
     *
     * @param parent the parent Node
     * @param predicate the predicate
     * @return the Node if found or null if not found
     */
    @Nullable // TODO! null again!
    public static <V> Node<V> findLastNode(@Nullable final Node<V> parent,
        @Nonnull final Predicate<Node<V>> predicate)
    {
        Objects.requireNonNull(predicate, "predicate");
        if (parent == null)
            return null;
        if (predicate.apply(parent))
            return parent;

        if (!hasChildren(parent))
            return null;

        return findLastNode(parent.getChildren(), predicate);
    }

    /**
     * Returns the last node underneath the given parents for which the given
     * predicate evaluates to true. If parents is null or empty or no node is
     * found the method returns null.
     *
     * @param parents the parent Nodes to look through
     * @param predicate the predicate
     * @return the Node if found or null if not found
     */
    @Nullable // TODO! null again!
    public static <V> Node<V> findLastNode(
        @Nullable final List<Node<V>> parents,
        @Nonnull final Predicate<Node<V>> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        if (parents == null)
            return null;
        if (parents.isEmpty())
            return null;

        final int parentsSize = parents.size();
        Node<V> found;
        for (int i = parentsSize - 1; i >= 0; i--) {
            found = findLastNode(parents.get(i), predicate);
            if (found != null)
                return found;
        }
        return null;
    }

    /**
     * Collects all nodes underneath the given parent for which the given
     * predicate evaluates to true.
     *
     * @param parent     the parent Node
     * @param predicate  the predicate
     * @param collection the collection to collect the found Nodes into
     * @return the same collection instance passed as a parameter
     */
    @Nonnull
    // TODO: possible null parent!
    public static <V, C extends Collection<Node<V>>> C collectNodes(
        @Nullable final Node<V> parent,
        @Nonnull final Predicate<Node<V>> predicate,
        @Nonnull final C collection)
    {
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(collection, "collection");
        if (parent == null)
            return collection;
        return hasChildren(parent)
            ? collectNodes(parent.getChildren(), predicate, collection)
            : collection;
    }

    /**
     * Returns the input text matched by the given node, with error correction.
     *
     * @param node the node
     * @param inputBuffer the underlying inputBuffer
     * @return a string with the matched input text (which can be empty)
     */
    @Nonnull
    public static String getNodeText(@Nonnull final Node<?> node,
        @Nonnull final InputBuffer inputBuffer) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(inputBuffer, "inputBuffer");
        if (!node.hasError())
            return inputBuffer.extract(node.getStartIndex(),
                node.getEndIndex());

        final StringBuilder sb = new StringBuilder();
        for (int i = node.getStartIndex(); i < node.getEndIndex(); i++) {
            final char c = inputBuffer.charAt(i);
            switch (c) {
                case Chars.EOI:
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Collects all nodes underneath the given parents for which the given
     * predicate evaluates to true.
     *
     * @param parents the parent Nodes to look through
     * @param predicate  the predicate
     * @param collection the collection to collect the found Nodes into
     * @return the same collection instance passed as a parameter
     */
    @Nonnull
    // TODO: possible null parents!
    public static <V, C extends Collection<Node<V>>> C collectNodes(
        @Nullable final List<Node<V>> parents,
        @Nonnull final Predicate<Node<V>> predicate,
        @Nonnull final C collection)
    {
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(collection, "collection");
        if (parents == null)
            return collection;
        if (parents.isEmpty())
            return collection;
        for (final Node<V> child: parents) {
            if (predicate.apply(child))
                collection.add(child);
            collectNodes(child, predicate, collection);
        }
        return collection;
    }

    /**
     * Returns true if this node is not null and has at least one child node.
     *
     * @param node a node
     * @return true if this node is not null and has at least one child node.
     */
    // TODO: null! Again!
    static boolean hasChildren(@Nullable final GraphNode<?> node)
    {
        return node != null && !node.getChildren().isEmpty();
    }
}

