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

package org.parboiled.support;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import javax.annotation.Nonnull;

/**
 * Simple specialization of a {@link Var} for StringBuilders.
 * Provides a few convenience helper methods.
 */
// TODO: value can be null, replace with empty StringBuilder
public class StringBuilderVar
    extends Var<StringBuilder>
{
    /**
     * Initializes a new StringVar with a null initial value.
     */
    public StringBuilderVar()
    {
        super(new Supplier<StringBuilder>()
        {
            @Override
            public StringBuilder get()
            {
                return new StringBuilder();
            }
        });
    }

    /**
     * Initializes a new StringBuilderVar with the given initial StringBuilder instance.
     *
     * @param value the initial value
     */
    public StringBuilderVar(final StringBuilder value)
    {
        super(Suppliers.ofInstance(value));
    }

    /**
     * Returns true if the wrapped string is either null or empty.
     *
     * @return true if the wrapped string is either null or empty
     */
    public boolean isEmpty()
    {
        return get().length() == 0;
    }

    /**
     * @return the String representation of the underlying StringBuilder.
     */
    public String getString()
    {
        return get().toString();
    }

    /**
     * @return the char[] representation of the underlying StringBuilder.
     */
    public char[] getChars()
    {
        final StringBuilder sb = get();
        final char[] buf = new char[sb.length()];
        sb.getChars(0, buf.length, buf, 0);
        return buf;
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
        get().append(text);
        return true;
    }

    /**
     * Appends the given string.
     * If this instance is currently uninitialized the given string is used for initialization.
     *
     * @param text the text to append
     * @return this instance
     */
    public StringBuilderVar appended(final String text)
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
        get().append(c);
        return true;
    }

    /**
     * Appends the given char.
     * If this instance is currently uninitialized the given char is used for initialization.
     *
     * @param c the char to append
     * @return this instance
     */
    public StringBuilderVar appended(final char c)
    {
        append(c);
        return this;
    }

    /**
     * Clears the contents of the wrapped StringBuilder.
     * If the instance is currently unintialized this method does nothing.
     *
     * @return true
     */
    public boolean clearContents()
    {
        get().setLength(0);
        return true;
    }

    /**
     * Clears the contents of the wrapped StringBuilder.
     * If the instance is currently unintialized this method does nothing.
     *
     * @return this instance
     */
    public StringBuilderVar contentsCleared()
    {
        get().setLength(0);
        return this;
    }

    @Nonnull
    @Override
    public StringBuilder get()
    {
        final StringBuilder superValue = super.get();
        return superValue == null ? new StringBuilder() : superValue;
    }
}

