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

import javax.annotation.Nullable;
import java.util.Objects;

public final class Tuple2<A, B>
{
    private final A first;
    private final B second;

    public Tuple2(final A first, final B second)
    {
        this.first = first;
        this.second = second;
    }

    public A getFirst()
    {
        return first;
    }

    // TODO: unused
    public B getSecond()
    {
        return second;
    }

    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (!(obj instanceof Tuple2))
            return false;
        if (this == obj)
            return true;
        final Tuple2<?, ?> other = (Tuple2<?, ?>) obj;
        return Objects.equals(first, other.first)
            && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(first, second);
    }

    @Override
    public String toString()
    {
        return "first: " + first + "; second: " + second;
    }
}
