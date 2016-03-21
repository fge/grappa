package com.github.fge.grappa.transform;

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.transform.load.ClassLoaderList;

import java.lang.reflect.Constructor;

public final class ParserFactory
{
    private final ClassLoaderList loaderList;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private ParserFactory(final Builder builder)
    {
        loaderList = builder.loaderListBuilder.build();
    }

    public <V, P extends BaseParser<V>> P getParser(final Class<P> parserClass,
        final Object... arguments)
        throws Exception
    {
        final ParserGenerator<V, P> generator = new ParserGenerator<>(
            parserClass, loaderList);

        final Class<? extends P> c = generator.transformParser();

        final Constructor<?> constructor = findConstructor(c, arguments);

        return (P) constructor.newInstance(arguments);
    }

    private static Constructor<?> findConstructor(final Class<?> c,
        final Object[] arguments)
    {
        Class<?>[] argumentTypes;

        for (final Constructor<?> constructor : c.getConstructors()) {
            argumentTypes = constructor.getParameterTypes();
            if (argumentsMatch(arguments, argumentTypes))
                return constructor;
        }
        throw new ParserTransformException("No constructor found for " + c
            + " and the given " + arguments.length + " arguments");
    }

    private static boolean argumentsMatch(final Object[] arguments,
        final Class<?>[] argumentTypes)
    {
        final int len = argumentTypes.length;

        if (len != arguments.length)
            return false;

        Object argument;
        Class<?> argumentType;

        for (int index = 0; index < len; index++) {
            argument = arguments[index];
            argumentType = argumentTypes[index];

            /*
             * If the argument is not null, check whether its class is exactly,
             * or a subtype of, the parameter class at the same index
             */
            if (argument != null
                && !argumentType.isAssignableFrom(argument.getClass()))
                return false;
            /*
             * If it is null, anything goes... Except if the type is a primitive
             */
            if (argument == null && argumentType.isPrimitive())
                return false;
        }

        /*
         * All arguments checked, compatible types; this is a match
         */
        return true;
    }

    public static final class Builder
    {
        private final ClassLoaderList.Builder loaderListBuilder
            = ClassLoaderList.newBuilder();

        private Builder()
        {
        }

        public Builder addClassLoader(final ClassLoader loader)
        {
            loaderListBuilder.addClassLoader(loader);
            return this;
        }

        public ParserFactory build()
        {
            return new ParserFactory(this);
        }
    }
}
