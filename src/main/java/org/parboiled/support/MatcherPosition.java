/*
 * Copyright (C) 2013 Chris Leishman
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

import com.github.parboiled1.grappa.annotations.WillBePrivate;
import org.parboiled.matchers.Matcher;

public final class MatcherPosition
{
    private final Matcher matcher;
    private final int index;

    @WillBePrivate(version = "1.1")
    protected MatcherPosition(final Matcher matcher, final int index)
    {
        this.matcher = matcher;
        this.index = index;
    }

    public static MatcherPosition at(final Matcher matcher, final int index)
    {
        return new MatcherPosition(matcher, index);
    }

    @Override
    public int hashCode()
    {
        return 31 * matcher.hashCode() + index;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof MatcherPosition))
            return false;
        if (this == obj)
            return true;
        final MatcherPosition other = (MatcherPosition) obj;
        return matcher == other.matcher && index == other.index;
    }
}
