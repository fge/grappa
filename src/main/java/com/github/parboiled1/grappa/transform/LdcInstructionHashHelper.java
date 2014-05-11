package com.github.parboiled1.grappa.transform;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hasher;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

/**
 * LDC instruction hash helper for {@link InstructionGroupHasher}
 *
 * @see MethodVisitor#visitLdcInsn(Object)
 */
@NotThreadSafe
public final class LdcInstructionHashHelper
{
    private final Map<Class<?>, Function<Object, Hasher>> functions;

    /**
     * Constructor
     *
     * @param hasher the hasher used by the {@link InstructionGroupHasher}
     * instance
     */
    public LdcInstructionHashHelper(@Nonnull final Hasher hasher)
    {
        Preconditions.checkNotNull(hasher);
        /*
         * Note that an ImmutableMap iterates in insertion order; this helps
         * here
         */
        final ImmutableMap.Builder<Class<?>, Function<Object, Hasher>> builder
            = ImmutableMap.builder();

        builder.put(Integer.class, forInteger(hasher))
            .put(Float.class, forFloat(hasher))
            .put(Long.class, forLong(hasher))
            .put(Double.class, forDouble(hasher))
            .put(String.class, forString(hasher))
            .put(Type.class, forType(hasher));

        functions = builder.build();
    }

    /**
     * Hash one LDC instruction
     *
     * @param object the operand to hash
     * @return the hasher instance (see {@link
     * #LdcInstructionHashHelper(Hasher)})
     */
    @Nonnull
    public Hasher hashLdc(@Nonnull final Object object)
    {
        Preconditions.checkNotNull(object);
        for (final Map.Entry<Class<?>, Function<Object, Hasher>> entry:
            functions.entrySet())
            if (Predicates.instanceOf(entry.getKey()).apply(object))
                // Note: below inserted by IDEA
                //noinspection ConstantConditions
                return entry.getValue().apply(object);
        throw new IllegalStateException("unsupported class "
            + object.getClass().getCanonicalName());
    }

    private static Function<Object, Hasher> forInteger(final Hasher hasher)
    {
        return new Function<Object, Hasher>()
        {
            @Override
            public Hasher apply(final Object input)
            {
                return hasher.putInt((Integer) input);
            }
        };
    }

    private static Function<Object, Hasher> forFloat(final Hasher hasher)
    {
        return new Function<Object, Hasher>()
        {
            @Override
            public Hasher apply(final Object input)
            {
                return hasher.putFloat((Float) input);
            }
        };
    }

    private static Function<Object, Hasher> forLong(final Hasher hasher)
    {
        return new Function<Object, Hasher>()
        {
            @Override
            public Hasher apply(final Object input)
            {
                return hasher.putLong((Long) input);
            }
        };
    }

    private static Function<Object, Hasher> forDouble(final Hasher hasher)
    {
        return new Function<Object, Hasher>()
        {
            @Override
            public Hasher apply(final Object input)
            {
                return hasher.putDouble((Double) input);
            }
        };
    }

    private static Function<Object, Hasher> forString(final Hasher hasher)
    {
        return new Function<Object, Hasher>()
        {
            @Override
            public Hasher apply(final Object input)
            {
                return hasher.putUnencodedChars((String) input);
            }
        };
    }

    private static Function<Object, Hasher> forType(final Hasher hasher)
    {
        return new Function<Object, Hasher>()
        {
            @Override
            public Hasher apply(final Object input)
            {
                final Type type = (Type) input;
                return hasher.putUnencodedChars(type.getInternalName());
            }
        };
    }
}
