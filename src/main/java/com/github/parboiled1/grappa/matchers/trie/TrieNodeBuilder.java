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

import java.nio.CharBuffer;
import java.util.Map;
import java.util.TreeMap;

public final class TrieNodeBuilder
{
    private boolean fullWord = false;

    private final Map<Character, TrieNodeBuilder> subnodes
        = new TreeMap<Character, TrieNodeBuilder>();

    public TrieNodeBuilder addWord(final String word)
    {
        doAddWord(CharBuffer.wrap(word));
        return this;
    }

    private void doAddWord(final CharBuffer buffer)
    {
        if (!buffer.hasRemaining()) {
            fullWord = true;
            return;
        }

        // Otherwise we need to continue; in any event we don't have a full
        // match at this point
        final char c = buffer.get();
        TrieNodeBuilder builder = subnodes.get(c);
        if (builder == null) {
            builder = new TrieNodeBuilder();
            subnodes.put(c, builder);
        }
        builder.doAddWord(buffer);
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
