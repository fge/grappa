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

import com.google.common.base.Strings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public final class TrieNodeBuilderTest
{
    private TrieBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = new TrieBuilder();
    }

    @Test
    public void cannotInsertPathologicalStrings()
    {
        try {
            builder.addWord(null);
            fail("No exception thrown!!");
        } catch (NullPointerException ignored) {
        }

        for (int i = 0; i < 2; i++)
            try {
                builder.addWord(Strings.repeat(" ", i));
                fail("No exception thrown!!");
            } catch (IllegalArgumentException e) {
                assertThat(e).hasMessage("strings in a trie must be two " +
                    "characters long or greater");
            }
    }
}
