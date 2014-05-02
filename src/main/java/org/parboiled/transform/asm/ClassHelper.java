package org.parboiled.transform.asm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;

public final class ClassHelper
{
    private final Type classType;
    private final boolean isInterface;
    private final int methodOpcode;
    private final Map<String, MethodDescriptor> methods = Maps.newHashMap();

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
        @Nonnull final Type returnType, @Nonnull final Type... arguments)
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(returnType);
        Preconditions.checkNotNull(arguments);

        final MethodDescriptor descriptor = methods.get(name);
        if (descriptor == null)
            throw new IllegalArgumentException("no method by name " + name
                + " in class " + classType.getClassName());

        return new MethodInsnNode(methodOpcode, classType.getInternalName(),
            name, descriptor.toString(), isInterface);
    }

    private static final class MethodDescriptor
    {
        private final Type returnType;
        private final Type[] argumentTypes;

        private MethodDescriptor(final Method method)
        {
            returnType = Type.getReturnType(method);
            argumentTypes = Type.getArgumentTypes(method);
        }

        @Override
        public String toString()
        {
            return Type.getMethodDescriptor(returnType, argumentTypes);
        }
    }
}
