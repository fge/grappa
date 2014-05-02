package org.parboiled.transform.asm;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ClassHelper
{
    private final Type classType;
    private final boolean isInterface;
    private final int methodOpcode;
    private final ListMultimap<String, MethodDescriptor> methods
        = ArrayListMultimap.create();

    ClassHelper(@Nonnull final Class<?> c)
    {
        Preconditions.checkNotNull(c);
        classType = Type.getType(c);
        isInterface = c.isInterface();
        methodOpcode = isInterface ? Opcodes.INVOKEINTERFACE
            : Opcodes.INVOKEVIRTUAL;

        for (final Method method: c.getDeclaredMethods())
            methods.put(method.getName(), new MethodDescriptor(method));
    }

    public MethodInsnNode methodCall(@Nonnull final String name,
        @Nonnull final Type returnType, @Nonnull final Type... args)
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(returnType);
        Preconditions.checkNotNull(args);

        final MethodDescriptor descriptor = findMethod(name, returnType, args);

        return new MethodInsnNode(methodOpcode, classType.getInternalName(),
            name, descriptor.toString(), isInterface);
    }

    private MethodDescriptor findMethod(final String name,
        final Type returnType, final Type... args)
    {
        for (final MethodDescriptor descriptor: methods.get(name))
            if (returnType.equals(descriptor.returnType)
                && Arrays.equals(args, descriptor.args))
                return descriptor;
        throw new IllegalArgumentException("no method with name " + name
            + ", return type " + returnType + " and arguments "
            + Arrays.toString(args) + " for class " + classType);
    }

    private static final class MethodDescriptor
    {
        private final Type returnType;
        private final Type[] args;

        private MethodDescriptor(final Method method)
        {
            returnType = Type.getReturnType(method);
            args = Type.getArgumentTypes(method);
        }

        @Override
        public String toString()
        {
            return Type.getMethodDescriptor(returnType, args);
        }
    }
}
