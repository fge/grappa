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
import java.util.Arrays;

public final class TrieNode
{
    private final boolean fullWord;

    private final char[] nextChars;
    private final TrieNode[] nextNodes;

    TrieNode(final boolean fullWord,
        final char[] nextChars, final TrieNode[] nextNodes)
    {
        this.fullWord = fullWord;
        this.nextChars = nextChars;
        this.nextNodes = nextNodes;
    }

    public int search(final String needle)
    {
        return doSearch(CharBuffer.wrap(needle), fullWord ? 0 : -1, 0);
    }

    private int doSearch(final CharBuffer buffer, final int matchedLength,
        final int currentLength)
    {
        final int nextLength = fullWord ? currentLength : matchedLength;

        if (!buffer.hasRemaining())
            return nextLength;

        final int index = Arrays.binarySearch(nextChars, buffer.get());

        return index < 0
            ? nextLength
            : nextNodes[index].doSearch(buffer, nextLength, currentLength + 1);
    }
}
