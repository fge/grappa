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

import java.lang.reflect.Array;

/**
 * DO NOT USE
 *
 * @param <T> element type
 *
 * @deprecated unused, will be removed in 1.1
 */
@Deprecated
public class ArrayBuilder<T> {

    private T[] array;

    public ArrayBuilder() {
        array = null;
    }

    public ArrayBuilder(final T... elements) {
        array = elements;
    }

    public T[] get() {
        return array;
    }

    @SuppressWarnings("unchecked")
    public ArrayBuilder<T> add(final T... elements) {
        if (elements == null) return this;
        if (array == null) {
            array = elements;
            return this;
        }
        final T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + elements.length);
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(elements, 0, newArray, array.length, elements.length);
        array = newArray;
        return this;
    }

    @SuppressWarnings("unchecked")
    public ArrayBuilder<T> addNonNulls(final T... elements) {
        if (elements == null) return this;
        if (array == null) {
            array = elements;
            return this;
        }
        int nonNulls = 0;
        for (final T element : elements) {
            if (element != null) nonNulls++;
        }
        if (nonNulls == 0) return this;

        final T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + nonNulls);
        System.arraycopy(array, 0, newArray, 0, array.length);
        for (int i = 0, j = array.length; i < elements.length; i++) {
            final T element = elements[i];
            if (element != null) {
                newArray[j++] = element;
            }
        }
        array = newArray;
        return this;
    }

}
