/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.parboiled1.grappa.misc;

import com.github.parboiled1.grappa.cleanup.WillBeRemoved;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.parboiled.annotations.ForBackwardsCompatibilityOnly;
import org.parboiled.common.Factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Backwards compatibility class
 *
 * <p>Will be removed when {@link Factory} will be removed.</p>
 *
 * @param <T>
 */
@ForBackwardsCompatibilityOnly
@WillBeRemoved(version = "1.1")
public final class SupplierAdapter<T>
    implements Supplier<T>, Factory<T>
{
    private final Factory<T> factory;

    public SupplierAdapter(@Nonnull final Factory<T> factory)
    {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Nullable
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
    @Nullable
    @Override
    public T get()
    {
        return create();
    }
}
