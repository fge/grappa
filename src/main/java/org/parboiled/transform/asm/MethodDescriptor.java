package org.parboiled.transform.asm;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.List;

@Immutable
public final class MethodDescriptor
{
    private static final Joiner JOINER = Joiner.on("");
    private static final Function<Type, String> TO_DESCRIPTOR
        = new Function<Type, String>()
        {
            @Override
            public String apply(final Type input)
            {
                return input.getDescriptor();
            }
        };

    private final int access;
    private final Type returnType;
    private final List<Type> arguments;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private MethodDescriptor(final Builder builder)
    {
        access = builder.access;
        returnType = builder.returnType;
        arguments = ImmutableList.copyOf(builder.arguments);
    }

    public String getSignature()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        JOINER.appendTo(sb, Iterables.transform(arguments, TO_DESCRIPTOR));
        sb.append(')').append(returnType.getDescriptor());
        return sb.toString();
    }

    @NotThreadSafe
    public static final class Builder
    {
        private int access = Opcodes.ACC_PUBLIC;
        private Type returnType = Type.VOID_TYPE;
        private final List<Type> arguments = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder withAccess(final int access)
        {
            // FIXME: perform checks
            this.access = access;
            return this;
        }

        public Builder withReturnType(@Nonnull final Type returnType)
        {
            Preconditions.checkNotNull(returnType);
            this.returnType = returnType;
            return this;
        }

        public Builder withReturnType(@Nonnull final Class<?> c)
        {
            Preconditions.checkNotNull(c);
            return withReturnType(Type.getType(c));
        }

        public Builder addArgument(@Nonnull final Type type)
        {
            Preconditions.checkNotNull(type);
            arguments.add(type);
            return this;
        }

        public Builder addArgument(@Nonnull final Class<?> c)
        {
            Preconditions.checkNotNull(c);
            return addArgument(Type.getType(c));
        }

        public Builder fromMethodNode(@Nonnull final MethodNode node)
        {
            final String descriptor = node.desc;
            returnType = Type.getReturnType(descriptor);
            arguments.clear();
            arguments.addAll(Arrays.asList(Type.getArgumentTypes(descriptor)));
            access = node.access;
            return this;
        }

        public MethodDescriptor build()
        {
            return new MethodDescriptor(this);
        }
    }
}
