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

package com.github.fge.grappa.support;

import com.google.common.base.Strings;

/**
 * Simple specialization of a {@link Var} for Strings. Provides a few convenience helper methods.
 */
// TODO: value can be null, replace with empty string
public class StringVar
    extends Var<String>
{

    /**
     * Initializes a new StringVar with a null initial value.
     */
    public StringVar()
    {
    }

    /**
     * Initializes a new StringVar with the given initial value.
     *
     * @param value the initial value
     */
    public StringVar(final String value)
    {
        super(value);
    }

    /**
     * Returns true if the wrapped string is either null or empty.
     *
     * @return true if the wrapped string is either null or empty
     */
    public boolean isEmpty()
    {
        return Strings.isNullOrEmpty(get());
    }

    /**
     * Appends the given string.
     * If this instance is currently uninitialized the given string is used for initialization.
     *
     * @param text the text to append
     * @return true
     */
    public boolean append(final String text)
    {
        return set(get() == null ? text : get() + text);
    }

    /**
     * Appends the given string.
     * If this instance is currently uninitialized the given string is used for initialization.
     *
     * @param text the text to append
     * @return this instance
     */
    public StringVar appended(final String text)
    {
        append(text);
        return this;
    }

    /**
     * Appends the given char.
     * If this instance is currently uninitialized the given char is used for initialization.
     *
     * @param c the char to append
     * @return true
     */
    public boolean append(final char c)
    {
        return set(get() == null ? String.valueOf(c) : get() + c);
    }

    /**
     * Appends the given char.
     * If this instance is currently uninitialized the given string is used for initialization.
     *
     * @param c the char to append
     * @return this instance
     */
    public StringVar appended(final char c)
    {
        append(c);
        return this;
    }
}

