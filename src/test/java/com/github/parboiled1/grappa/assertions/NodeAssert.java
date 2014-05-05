package com.github.parboiled1.grappa.assertions;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.assertj.core.api.AbstractAssert;
import org.parboiled.Node;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

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
    NodeAssert(final Node<V> actual)
    {
        super(Preconditions.checkNotNull(actual), NodeAssert.class);
    }

    NodeAssert<V> hasLabel(@Nonnull final String label)
    {
        final String thisLabel = actual.getLabel();
        assertThat(thisLabel).overridingErrorMessage(
            "node's label is null! I didn't expect it to be"
        ).isNotNull();
        assertThat(thisLabel).overridingErrorMessage(
            "node's label is not what was expected!\n"
            + "Expected: '%s'\nActual  : '%s'\n", label, thisLabel
        ).isEqualTo(label);
        return this;
    }

    NodeAssert<V> hasLabel(@Nonnull final Optional<String> label)
    {
        return label.isPresent() ? hasLabel(label.get()) : this;
    }
}
