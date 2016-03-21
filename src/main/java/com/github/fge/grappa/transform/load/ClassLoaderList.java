package com.github.fge.grappa.transform.load;

import com.github.fge.grappa.transform.ParserTransformException;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ClassLoaderList
{
    private final List<ClassLoader> classLoaders;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private ClassLoaderList(final Builder builder)
    {
        classLoaders = ImmutableList.copyOf(builder.classLoaders);
    }

    public InputStream getInputStream(final Class<?> c)
    {
        Objects.requireNonNull(c);
        final String resource = toClassResource(c);

        InputStream ret;

        for (final ClassLoader loader: classLoaders) {
            ret = loader.getResourceAsStream(resource);
            if (ret != null)
                return ret;
        }

        throw new ParserTransformException("unable to locate class file for "
            + "class " + c.getName());
    }

    public static final class Builder
    {
        private final List<ClassLoader> classLoaders = new ArrayList<>();

        private Builder()
        {
            classLoaders.add(ClassLoaderList.class.getClassLoader());
            classLoaders.add(Thread.currentThread().getContextClassLoader());
            classLoaders.add(ClassLoader.getSystemClassLoader());
        }

        public Builder addClassLoader(final ClassLoader classLoader)
        {
            classLoaders.add(Objects.requireNonNull(classLoader));
            return this;
        }

        public ClassLoaderList build()
        {
            return new ClassLoaderList(this);
        }
    }

    private static String toClassResource(final Class<?> c)
    {
        return c.getName().replace('.', '/') + ".class";
    }
}
