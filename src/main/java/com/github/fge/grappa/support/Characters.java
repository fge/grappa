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

import java.util.Arrays;
import java.util.Objects;

/**
 * An immutable, set-like aggregation of (relatively few) characters that allows
 * for an inverted semantic ("all chars except these few").
 */
public final class Characters
{
    private static final char[] NO_CHARS = new char[0];

    /**
     * The empty Characters set
     */
    public static final Characters NONE = new Characters(false, NO_CHARS);

    /**
     * The Characters set including all character.
     */
    public static final Characters ALL = new Characters(true, NO_CHARS);

    // if the set is subtractive its semantics change from "includes all
    // characters in the set" to "includes all characters not in the set"
    private final boolean subtractive;
    private final char[] chars;

    /**
     * Creates a new Characters instance containing only the given char.
     *
     * @param c the char
     * @return a new Characters object
     */
    public static Characters of(final char c)
    {
        return new Characters(false, new char[]{ c });
    }

    /**
     * Creates a new Characters instance containing only the given chars.
     *
     * @param chars the chars
     * @return a new Characters object
     */
    public static Characters of(final char... chars)
    {
        final int length = chars.length;
        if (length == 0)
            return NONE;
        final char[] array = Arrays.copyOf(chars, length);
        Arrays.sort(array);
        return new Characters(false, array);
    }

    /**
     * Creates a new Characters instance containing only the given chars.
     *
     * @param chars the chars
     * @return a new Characters object
     */
    public static Characters of(final String chars)
    {
        return chars.isEmpty() ? NONE : of(chars.toCharArray());
    }

    /**
     * Creates a new Characters instance containing all characters minus the given ones.
     *
     * @param chars the chars to NOT include
     * @return a new Characters object
     */
    public static Characters allBut(final char... chars)
    {
        final int length = chars.length;
        if (length == 0)
            return ALL;
        final char[] array = Arrays.copyOf(chars, length);
        Arrays.sort(array);
        return new Characters(true, array);
    }

    private Characters(final boolean subtractive, final char[] chars)
    {
        this.subtractive = subtractive;
        this.chars = Objects.requireNonNull(chars, "chars");
    }

    /**
     * @return true if the set is subtractive
     */
    public boolean isSubtractive()
    {
        return subtractive;
    }

    /**
     * Returns the characters in this set, if it is additive.
     * If the set is subtractive the method returns the characters <b>not</b> in the set.
     *
     * @return the characters
     */
    public char[] getChars()
    {
        return chars;
    }

    /**
     * Determines whether this instance contains the given character.
     *
     * @param c the character to check for
     * @return true if this instance contains c
     */
    public boolean contains(final char c)
    {
        final int index = Arrays.binarySearch(chars, c);
        return (index == -1) == subtractive;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(subtractive ? "![" : "[");
        for (final char c : chars) {
            sb.append(Chars.escape(c));
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof Characters))
            return false;
        final Characters other = (Characters) obj;
        return subtractive == other.subtractive
            && Arrays.equals(chars, other.chars);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(chars) + (subtractive ? 31 : 0);
    }
}
