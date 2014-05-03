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

import com.github.parboiled1.grappa.cleanup.WillBePrivate;
import com.google.common.base.Objects;

import javax.annotation.Nullable;

public final class Tuple2<A, B>
{
    @WillBePrivate(version = "1.1")
    public final A a;
    @WillBePrivate(version = "1.1")
    public final B b;

    public Tuple2(final A a, final B b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (!(obj instanceof Tuple2))
            return false;
        if (this == obj)
            return true;
        final Tuple2<?, ?> other = (Tuple2<?, ?>) obj;
        return Objects.equal(a, other.a) && Objects.equal(b, other.b);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(a, b);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(getClass())
            .add("a", a).add("b", b).toString();
    }
}
