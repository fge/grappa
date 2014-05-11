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

package com.github.parboiled1.grappa.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;

import javax.annotation.Nullable;

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

    public NodeAssert(final Node<V> actual, final InputBuffer buffer)
    {
        super(actual, NodeAssert.class);
        this.buffer = buffer;
    }

    public void hasLabel(final SoftAssertions soft,
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
    }

    public void hasMatch(final SoftAssertions soft,
        final String expectedMatch)
    {
        final String actualMatch
            = buffer.extract(actual.getStartIndex(), actual.getEndIndex());
        soft.assertThat(actualMatch).overridingErrorMessage(
            "rule did not match what was expected!\n"
            + "Expected: -->%s<--\nActual  : -->%s<--\n",
            expectedMatch, actualMatch
        ).isEqualTo(expectedMatch);
    }

    public void hasValue(final SoftAssertions soft,
        @Nullable final V expectedValue)
    {
        final V actualValue = actual.getValue();
        soft.assertThat(actualValue).as("expected a value").isNotNull();
        if (actualValue != null)
            soft.assertThat(actualValue).as("node value check")
                .isEqualTo(expectedValue);
    }
}
