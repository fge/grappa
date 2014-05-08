package org.parboiled.transform.asm;

import com.github.parboiled1.grappa.cleanup.DoNotUse;
import com.github.parboiled1.grappa.cleanup.WillBeRemoved;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.util.Map;

public final class AsmHelper
{
    private static final Map<Integer, Integer> LOADING_OPCODES;

    static {
        final ImmutableMap.Builder<Integer, Integer> builder
            = ImmutableMap.builder();

        builder.put(Type.BOOLEAN, Opcodes.ILOAD);
        builder.put(Type.BYTE, Opcodes.ILOAD);
        builder.put(Type.CHAR, Opcodes.ILOAD);
        builder.put(Type.SHORT, Opcodes.ILOAD);
        builder.put(Type.INT, Opcodes.ILOAD);
        builder.put(Type.DOUBLE, Opcodes.DLOAD);
        builder.put(Type.FLOAT, Opcodes.FLOAD);
        builder.put(Type.LONG, Opcodes.LLOAD);
        builder.put(Type.OBJECT, Opcodes.ALOAD);
        builder.put(Type.ARRAY, Opcodes.ALOAD);

        LOADING_OPCODES = builder.build();
    }

    private AsmHelper()
    {
    }

    @Deprecated
    @DoNotUse
    @WillBeRemoved(version = "1.1")
    public static ClassHelper classHelper(@Nonnull final Class<?> c)
    {
        return new ClassHelper(c);
    }

    public static int loadingOpcodeFor(@Nonnull final Type type)
    {
        Preconditions.checkNotNull(type);
        // Will throw IllegalStateException if optional .isAbsent()
        return Optional.fromNullable(LOADING_OPCODES.get(type.getSort())).get();
    }
}
