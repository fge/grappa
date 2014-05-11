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

package com.github.parboiled1.grappa.assertions.verify;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.parboiled1.grappa.assertions.NodeAssert;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class NodeVerifier<V>
    implements Verifier<Node<V>>
{
    @JsonIgnore
    private InputBuffer buffer;
    private String label;
    private String match;
    private V value;
    private final List<NodeVerifier<V>> children = Lists.newArrayList();

    void setLabel(final String label)
    {
        this.label = label;
    }

    void setMatch(final String match)
    {
        this.match = match;
    }

    void setChildren(final List<NodeVerifier<V>> children)
    {
        this.children.addAll(children);
    }

    void setValue(final V value)
    {
        this.value = value;
    }

    public NodeVerifier<V> setBuffer(final InputBuffer buffer)
    {
        this.buffer = buffer;
        return this;
    }

    @Override
    public void verify(@Nonnull final SoftAssertions soft,
        @Nullable final Node<V> toVerify)
    {
        Preconditions.checkNotNull(soft);
        if (toVerify == null) {
            soft.assertThat(toVerify).as("parse tree is not null").isNotNull();
            return;
        }
        final NodeAssert<V> nodeAssert = new NodeAssert<V>(toVerify, buffer);
        if (label != null)
            nodeAssert.hasLabel(soft, label);
        if (match != null)
            nodeAssert.hasMatch(soft, match);
        if (value != null)
            nodeAssert.hasValue(soft, value);
        final List<Node<V>> nodeChildren = toVerify.getChildren();
        soft.assertThat(nodeChildren).as("same number of children nodes")
            .hasSameSizeAs(children);
        final int size = Math.min(nodeChildren.size(), children.size());
        for (int index = 0; index < size; index++)
            children.get(index).setBuffer(buffer)
                .verify(soft, nodeChildren.get(index));

    }
}
