/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package org.parboiled.common;

import com.github.parboiled1.grappa.annotations.DoNotUse;
import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.github.parboiled1.grappa.annotations.WillBeRemoved;

import javax.annotation.Nullable;

/**
 * A simple container holding a reference to another object.
 *
 * @param <T>
 */
public class Reference<T>
{
    private T value;

    /**
     * Create a new Reference with a null value.
     */
    public Reference()
    {
    }

    /**
     * Create a new Reference to the given value object.
     *
     * @param value the value object
     */
    public Reference(@Nullable final T value)
    {
        this.value = value;
    }

    /**
     * Sets this references value field to null.
     *
     * @return true
     */
    @WillBeFinal(version = "1.1.0")
    public boolean clear()
    {
        return set(null);
    }

    /**
     * Sets this references value object to the given instance.
     *
     * @param value the value
     * @return true
     */
    @WillBeFinal(version = "1.1.0")
    public boolean set(@Nullable final T value)
    {
        this.value = value;
        return true;
    }

    /**
     * Retrieves this references value object.
     *
     * @return the target
     */
    @Nullable
    @WillBeFinal(version = "1.1.0")
    public T get()
    {
        return value;
    }

    /**
     * DO NOT USE
     *
     * Retrieves this references value field and clears it.
     * Equivalent to getAndSet(null).
     *
     * @return the target
     *
     * @deprecated not used!
     */
    @DoNotUse
    @Deprecated
    @WillBeRemoved(version = "1.1")
    public T getAndClear()
    {
        return getAndSet(null);
    }

    /**
     * Replaces this references value with the given one.
     *
     * @param value the new value
     * @return the previous value
     */
    @WillBeFinal(version = "1.1")
    public T getAndSet(final T value)
    {
        final T ret = this.value;
        this.value = value;
        return ret;
    }

    /**
     * Replaces this references value with the given one.
     *
     * @param value the new value
     * @return the new value
     */
    @DoNotUse
    @Deprecated
    @WillBeRemoved(version = "1.1")
    public T setAndGet(final T value)
    {
        return this.value = value;
    }

    /**
     * @return true if this Reference holds a non-null value
     */
    @WillBeFinal(version = "1.1")
    public boolean isSet()
    {
        return value != null;
    }

    /**
     * @return true if this Reference holds a null value
     */
    @Deprecated
    @DoNotUse
    @WillBeRemoved(version = "1.1")
    public boolean isNotSet()
    {
        return value == null;
    }
}
