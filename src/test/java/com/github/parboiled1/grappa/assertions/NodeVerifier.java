package com.github.parboiled1.grappa.assertions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;

import javax.annotation.Nonnull;
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
        @Nonnull final Node<V> toVerify)
    {
        Preconditions.checkNotNull(soft);
        Preconditions.checkNotNull(toVerify);
        final NodeAssert<V> nodeAssert = new NodeAssert<V>(toVerify, buffer);
        if (label != null)
            nodeAssert.hasLabel(soft, label);
        if (match != null)
            nodeAssert.hasMatch(soft, match);
        final List<Node<V>> nodeChildren = toVerify.getChildren();
        soft.assertThat(nodeChildren).hasSameSizeAs(children);
        final int size = Math.min(nodeChildren.size(), children.size());
        for (int index = 0; index < size; index++)
            children.get(index).setBuffer(buffer)
                .verify(soft, nodeChildren.get(index));

    }
}
