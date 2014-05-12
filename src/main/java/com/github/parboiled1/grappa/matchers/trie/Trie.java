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

import javax.annotation.Nonnull;

public final class Trie
{
    private final int maxLength;
    private final TrieNode node;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public int search(final String needle)
    {
        return node.search(needle);
    }

    private Trie(final Builder builder)
    {
        maxLength = builder.maxLength;
        node = builder.nodeBuilder.build();
    }

    public static final class Builder
    {
        private int maxLength = 0;
        private final TrieNodeBuilder nodeBuilder
            = new TrieNodeBuilder();

        private Builder()
        {
        }

        public Builder addWord(@Nonnull final String word)
        {
            maxLength = Math.max(maxLength, word.length());
            nodeBuilder.addWord(word);
            return this;
        }

        public Trie build()
        {
            return new Trie(this);
        }
    }
}
