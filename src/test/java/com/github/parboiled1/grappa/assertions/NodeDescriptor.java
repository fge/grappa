package com.github.parboiled1.grappa.assertions;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.parboiled.Node;

import javax.annotation.Nonnull;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public abstract class NodeDescriptor<V>
{
    public static <E> Builder<E> newBuilder()
    {
        return new Builder<E>();
    }

    public abstract void verify(@Nonnull final Optional<Node<V>> node);

    public static final class Builder<V>
    {
        private String label;
        private final List<Builder<V>> children = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder<V> withLabel(@Nonnull final String label)
        {
            this.label = Preconditions.checkNotNull(label);
            return this;
        }

        public Builder<V> withChildNode(@Nonnull final Builder<V> child)
        {
            Preconditions.checkNotNull(child);
            children.add(child);
            return this;
        }

        public NodeDescriptor<V> build()
        {
            return new WithNode<V>(this);
        }
    }

    private static final class WithNode<E>
        extends NodeDescriptor<E>
    {
        private final Optional<String> label;
        private final List<NodeDescriptor<E>> children;

        private WithNode(final Builder<E> builder)
        {
            label = Optional.fromNullable(builder.label);
            final ImmutableList.Builder<NodeDescriptor<E>> listBuilder
                = ImmutableList.builder();
            for (final Builder<E> element: builder.children)
                listBuilder.add(element.build());
            children = listBuilder.build();
        }

        @Override
        public void verify(@Nonnull final Optional<Node<E>> node)
        {
            assertThat(node.isPresent()).overridingErrorMessage(
                "expected to have a node, but I didn't!"
            ).isTrue();
            final NodeAssert<E> nodeAssert = new NodeAssert<E>(node.get());
            nodeAssert.hasLabel(label);
            verifyChildren(node.get());
        }

        private void verifyChildren(final Node<E> node)
        {
            final List<Node<E>> nodeChildren = node.getChildren();
            final int size = Math.max(children.size(), nodeChildren.size());
            NodeDescriptor<E> childDescriptor;
            Optional<Node<E>> childNode;

            for (int i = 0; i < size; i++) {
                childDescriptor = Optional.fromNullable(Iterables.get(children,
                    i, null)).or(new NoNode<E>(i));
                childNode = Optional.fromNullable(Iterables.get(nodeChildren, i,
                    null));
                childDescriptor.verify(childNode);
            }
        }
    }

    private static final class NoNode<E>
        extends NodeDescriptor<E>
    {
        private final int index;

        private NoNode(final int index)
        {
            this.index = index;
        }

        @Override
        public void verify(@Nonnull final Optional<Node<E>> node)
        {
            fail("did not expect a node at index " + index);
        }
    }
}
