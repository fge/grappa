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

    public MethodInsnNode voidMethodCall(@Nonnull final String name,
        @Nonnull final Type... args)
    {
        return methodCall(name, Type.VOID_TYPE, args);
    }

    public MethodInsnNode methodCall(@Nonnull final String name,
        @Nonnull final Class<?> returnType, @Nonnull final Class<?>... args)
    {
        Preconditions.checkNotNull(returnType);
        Preconditions.checkNotNull(args);

        final Type[] typeArgs = new Type[args.length];
        for (int i = 0; i < args.length; i++)
            typeArgs[i] = Type.getType(args[i]);

        return methodCall(name, Type.getType(returnType), typeArgs);
    }

    public MethodInsnNode voidMethodCall(@Nonnull final String name,
        @Nonnull final Class<?>... args)
    {
        return methodCall(name, void.class, args);
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
