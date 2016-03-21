package com.github.fge.grappa.transform.load;

import com.github.fge.grappa.transform.ParserTransformException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A limited-purpose, simple wrapper over a list of {@link ClassLoader}s
 *
 * <p>Note that by default, an instance of this class will always contain the
 * following classloaders, in this order:</p>
 *
 * <ul>
 *     <li>this class's classloader ({@code ClassLoader.class.getClassLoader()};
 *     </li>
 *     <li>the current thread's classloader ({@code
 *     Thread.currentThread().getContextClassLoader()};</li>
 *     <li>the system classloader ({@code ClassLoader.getSystemClassLoader()}.
 *     </li>
 * </ul>
 *
 * <p>Any classloader you add (using {@link Builder#addClassLoader(ClassLoader)}
 * will be added <em>after</em> those three.</p>
 *
 * <p>To build an instance, use:</p>
 *
 * <pre>
 *     final ClassLoaderList loaderList = ClassLoaderList.newBuilder()
 *         .addClassLoader(loader1)
 *         .addClassLoader(loader2) // etc etc
 *         .build();
 * </pre>
 */
@Beta
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
