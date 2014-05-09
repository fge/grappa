package com.github.parboiled1.grappa.misc;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.parboiled.common.Factory;

import javax.annotation.Nonnull;

public final class SupplierAdapter<T>
    implements Supplier<T>, Factory<T>
{
    private final Factory<T> factory;

    public SupplierAdapter(@Nonnull final Factory<T> factory)
    {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public T create()
    {
        return factory.create();
    }

    /**
     * Retrieves an instance of the appropriate type. The returned object may or
     * may not be a new instance, depending on the implementation.
     *
     * @return an instance of the appropriate type
     */
    @Override
    public T get()
    {
        return create();
    }
}
