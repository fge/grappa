/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.parboiled1.grappa.matchers.trie;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.matchers.AbstractMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.FirstOfStringsMatcher;
import org.parboiled.matchervisitors.MatcherVisitor;
import org.parboiled.parserunners.RecoveringParseRunner;

import javax.annotation.concurrent.Immutable;

/**
 * The trie matcher
 *
 * <p>Note that there are a few crucial differences compared to a {@link
 * FirstOfStringsMatcher}:</p>
 *
 * <ul>
 *     <li>this matcher is insensitive about the ordering of its arguments;
 *     unlike {@link FirstOfMatcher}, for instance, it doesn't care about the
 *     order of appearance of strings with a common prefix;</li>
 *     <li>at this moment, it does not play well with a {@link
 *     RecoveringParseRunner} (a {@link FirstOfStringsMatcher} keeps all
 *     submatches information by its virtue of inheriting {@link
 *     FirstOfMatcher}, which has all character subrules).</li>
 * </ul>
 *
 * @since 1.0.0-beta.6
 */
@Immutable
@Beta
public final class TrieMatcher
    extends AbstractMatcher
{
    private final Trie trie;

    public TrieMatcher(final Trie trie)
    {
        super("Trie (" + Preconditions.checkNotNull(trie).getNrWords()
            + " strings)");
        this.trie = trie;
    }

    /**
     * Tries a match on the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if the match was successful
     */
    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        /*
         * Since the trie knows about the length of its possible longest match,
         * extract that many characters from the buffer. Remind that .extract()
         * will adjust illegal indices automatically.
         */
        final int maxLength = trie.getMaxLength();
        final int index = context.getCurrentIndex();
        final String input = context.getInputBuffer()
            .extract(index, index + maxLength);

        /*
         * We now just have to trie and search... (pun intended)
         */
        final int ret = trie.search(input);
        if (ret == -1)
            return false;

        /*
         * and since the result, when positive, is the length of the match,
         * advance the index in the buffer by that many positions.
         */
        context.advanceIndex(ret);
        return true;
    }

    /**
     * Accepts the given matcher visitor.
     *
     * @param visitor the visitor
     * @return the value returned by the given visitor
     */
    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        return null;
    }
}
