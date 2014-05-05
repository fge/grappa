package com.github.parboiled1.grappa.assertions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public abstract class ParseTreeAssert<V>
{
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final String RESOURCE_PREFIX = "/parseTrees/";

    public abstract void verify(@Nonnull final Optional<Node<V>> node);

    public static final class Builder<V>
    {
        private String label;
        private String match;
        private final List<Builder<V>> children = Lists.newArrayList();

        Builder()
        {
        }

        @JsonProperty("label")
        public Builder<V> withLabel(@Nonnull final String label)
        {
            this.label = Preconditions.checkNotNull(label);
            return this;
        }

        @JsonProperty("children")
        public Builder<V> withChildren(@Nonnull final List<Builder<V>> list)
        {
            Preconditions.checkNotNull(list);
            children.addAll(list);
            return this;
        }

        @JsonProperty("match")
        public Builder<V> withMatch(@Nonnull final String match)
        {
            this.match = Preconditions.checkNotNull(match);
            return this;
        }

        public ParseTreeAssert<V> build(final InputBuffer buffer)
        {
            return new WithNode<V>(this, buffer);
        }
    }

    @ParametersAreNonnullByDefault
    private static final class WithNode<E>
        extends ParseTreeAssert<E>
    {
        private final InputBuffer buffer;
        private final Optional<String> label;
        private final Optional<String> match;
        private final List<ParseTreeAssert<E>> children;

        private WithNode(final Builder<E> builder,
            final InputBuffer buffer)
        {
            label = Optional.fromNullable(builder.label);
            match = Optional.fromNullable(builder.match);
            final ImmutableList.Builder<ParseTreeAssert<E>> listBuilder
                = ImmutableList.builder();
            for (final Builder<E> element: builder.children)
                listBuilder.add(element.build(buffer));
            children = listBuilder.build();
            this.buffer = buffer;
        }

        @Override
        public void verify(final Optional<Node<E>> node)
        {
            assertThat(node.isPresent()).overridingErrorMessage(
                "expected to have a node, but I didn't!"
            ).isTrue();
            final NodeAssert<E> nodeAssert
                = new NodeAssert<E>(node.get(), buffer);
            nodeAssert.hasLabel(label).hasMatch(match);
            verifyChildren(node.get());
        }

        private void verifyChildren(final Node<E> node)
        {
            final List<Node<E>> nodeChildren = node.getChildren();
            final int size = Math.max(children.size(), nodeChildren.size());
            ParseTreeAssert<E> childDescriptor;
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
        extends ParseTreeAssert<E>
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

    public static <E> ParseTreeAssert<E> read(final String resourceName,
        final InputBuffer buffer)
        throws IOException
    {
        final String path = RESOURCE_PREFIX + resourceName;
        final TypeReference<Builder<E>> typeRef
            = new TypeReference<Builder<E>>() {};

        final Closer closer = Closer.create();
        final InputStream in;
        final Builder<E> builder;

        try {
            in = closer.register(ParseTreeAssert.class
                .getResourceAsStream(path));
            if (in == null)
                throw new IOException("resource " + path + " not found");
            builder = MAPPER.readValue(in, typeRef);
            return builder.build(buffer);
        } finally {
            closer.close();
        }
    }
}
