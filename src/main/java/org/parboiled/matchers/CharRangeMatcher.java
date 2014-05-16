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

package org.parboiled.matchers;

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.github.parboiled1.grappa.annotations.WillBePrivate;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.matchervisitors.MatcherVisitor;

import static org.parboiled.support.Chars.escape;

/**
 * A {@link Matcher} matching a single character out of a given range of characters.
 */
@WillBeFinal(version = "1.1")
public class CharRangeMatcher
    extends AbstractMatcher
{
    @WillBePrivate(version = "1.1")
    public final char cLow;
    @WillBePrivate(version = "1.1")
    public final char cHigh;

    public CharRangeMatcher(final char cLow, final char cHigh)
    {
        super(escape(cLow) + ".." + escape(cHigh));
        Preconditions.checkArgument(cLow < cHigh);
        this.cLow = cLow;
        this.cHigh = cHigh;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final char c = context.getCurrentChar();
        if (c < cLow || c > cHigh)
            return false;

        context.advanceIndex(1);
        context.createNode();
        return true;
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}