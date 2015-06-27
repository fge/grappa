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

package com.github.fge.grappa.matchers.trie;

import com.google.common.annotations.Beta;

import java.util.Map;
import java.util.TreeMap;

/**
 * A builder for a {@link TrieNode} tree
 *
 * <p>The tree is built "in depth"; each character of a string will create a new
 * builder unless there is already a builder for that character.</p>
 *
 * <p>In the event where the matching is case insensitive (the argument to
 * {@link TrieBuilder#TrieBuilder(boolean)} is {@code true}), a further node
 * will be created when appropriate.</p>
 *
 * <p>When {@link #build()} is called, the whole tree is built from the leaves
 * up to the root.</p>
 *
 * @since 1.0.0-beta.6
 *
 * @see Character#isUpperCase(char)
 * @see Character#isLowerCase(char)
 */
@Beta
public final class TrieNodeBuilder
{
    private boolean fullWord = false;

    private final Map<Character, TrieNodeBuilder> subnodes
        = new TreeMap<>();

    TrieNodeBuilder addWord(final CharSequence word, final boolean ignoreCase)
    {
        doAddWord(word, ignoreCase, 0);
        return this;
    }

    /**
     * Add a word
     *
     * @param word the word as a {@link CharSequence}
     * @param ignoreCase whether the trie ignores case
     * @param index the current index in the sequence
     */
    private void doAddWord(final CharSequence word, final boolean ignoreCase,
        final int index)
    {
        if (word.length() == index) {
            fullWord = true;
            return;
        }

        char c;
        TrieNodeBuilder builder;

        c = word.charAt(index);
        builder = subnodes.get(c);
        if (builder == null) {
            builder = new TrieNodeBuilder();
            subnodes.put(c, builder);
        }
        builder.doAddWord(word, ignoreCase, index + 1);

        if (!ignoreCase)
            return;

        final boolean upper = Character.isUpperCase(c);
        final boolean lower = Character.isLowerCase(c);

        if (upper == lower)
            return;

        if (Character.isUpperCase(c)) {
            c = Character.toLowerCase(c);
            builder = subnodes.get(c);
            if (builder == null) {
                builder = new TrieNodeBuilder();
                subnodes.put(c, builder);
            }
            builder.doAddWord(word, ignoreCase, index + 1);
        }

        if (Character.isLowerCase(c)) {
            c = Character.toUpperCase(c);
            builder = subnodes.get(c);
            if (builder == null) {
                builder = new TrieNodeBuilder();
                subnodes.put(c, builder);
            }
            builder.doAddWord(word, ignoreCase, index + 1);
        }
    }

    public TrieNode build()
    {
        final char[] nextChars = new char[subnodes.size()];
        final TrieNode[] nextNodes = new TrieNode[subnodes.size()];

        int index = 0;
        for (final Map.Entry<Character, TrieNodeBuilder> entry:
            subnodes.entrySet()) {
            nextChars[index] = entry.getKey();
            nextNodes[index] = entry.getValue().build();
            index++;
        }
        return new TrieNode(fullWord, nextChars, nextNodes);
    }
}
