package com.github.parboiled1.grappa.assertions;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;

import javax.annotation.Nonnull;

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
    private final InputBuffer buffer;

    NodeAssert(final Node<V> actual, final InputBuffer buffer)
    {
        super(Preconditions.checkNotNull(actual), NodeAssert.class);
        this.buffer = buffer;
    }

    private NodeAssert<V> doHasLabel(final SoftAssertions soft,
        final String expectedLabel)
    {
        final String actualLabel = actual.getLabel();
        soft.assertThat(actualLabel).overridingErrorMessage(
            "node's label is null! I didn't expect it to be"
        ).isNotNull();
        soft.assertThat(actualLabel).overridingErrorMessage(
            "node's label is not what was expected!\n"
            + "Expected: '%s'\nActual  : '%s'\n", expectedLabel, actualLabel
        ).isEqualTo(expectedLabel);
        return this;
    }

    NodeAssert<V> hasLabel(@Nonnull final SoftAssertions soft,
        @Nonnull final Optional<String> label)
    {
        Preconditions.checkNotNull(soft);
        Preconditions.checkNotNull(label);
        return label.isPresent() ? doHasLabel(soft, label.get()) : this;
    }

    private NodeAssert<V> doHasMatch(final SoftAssertions soft,
        final String expectedMatch)
    {
        final String actualMatch
            = buffer.extract(actual.getStartIndex(), actual.getEndIndex());
        soft.assertThat(actualMatch).overridingErrorMessage(
            "rule did not match what was expected!\n"
            + "Expected: -->%s<--\nActual  : -->%s<--\n",
            expectedMatch, actualMatch
        ).isEqualTo(expectedMatch);
        return this;
    }

    NodeAssert<V> hasMatch(@Nonnull final SoftAssertions soft,
        @Nonnull final Optional<String> match)
    {
        return match.isPresent() ? doHasMatch(soft, match.get()) : this;
    }
}
