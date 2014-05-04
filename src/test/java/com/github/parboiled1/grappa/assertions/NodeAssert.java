package com.github.parboiled1.grappa.assertions;

import com.google.common.base.Preconditions;
import org.assertj.core.api.AbstractAssert;
import org.parboiled.Node;

/**
 * A parse tree dump has such a node at its root
 *
 * <p>TODO: not sure about the not null assertion here</p>
 *
 * @param <V> values produced by this node tree
 */
public final class NodeAssert<V>
    extends AbstractAssert<NodeAssert<V>, Node<V>>
{
    private NodeAssert (final Node<V> actual)
    {
        super(Preconditions.checkNotNull(actual), NodeAssert.class);
    }
}
