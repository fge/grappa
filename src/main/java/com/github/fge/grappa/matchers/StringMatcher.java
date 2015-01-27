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

package com.github.fge.grappa.matchers;

import com.github.fge.grappa.matchers.delegate.SequenceMatcher;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/**
 * A {@link SequenceMatcher} specialization for sequences of CharMatchers. Performs fast string matching if the
 * current context has it enabled.
 */
public final class StringMatcher
    extends SequenceMatcher
{
    private final char[] characters;

    public StringMatcher(final Rule[] charMatchers, final char[] characters)
    {
        super(Preconditions.checkNotNull(charMatchers, "charMatchers"));
        this.characters = characters;
    }
    
    @Override
    public MatcherType getType()
    {
        return MatcherType.TERMINAL;
    }

    public char[] getCharacters()
    {
        return characters;
    }

    @Override
    public String getLabel()
    {
        return super.getLabel() != null
            ? super.getLabel()
            : '"' + String.valueOf(characters) + '"';
    }

    @Override
    public boolean hasCustomLabel()
    {
        return true;
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        if (!context.getInputBuffer().test(context.getCurrentIndex(),
            characters))
            return false;
        context.advanceIndex(characters.length);
        context.createNode();
        return true;
    }

    @Override
    public boolean canMatchEmpty()
    {
        // TODO: check, but that should be the case
        return false;
    }
}
