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

package com.github.fge.grappa.assertions.verify;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.support.ParsingResult;

import javax.annotation.Nonnull;
import java.util.List;

public final class ParsingResultVerifier<V>
    implements Verifier<ParsingResult<V>>
{
    private CharSequenceInputBuffer buffer;
    private boolean hasMatch;
    private NodeVerifier<V> parseTree;
    private final List<ParseErrorVerifier<?>> errors = Lists.newArrayList();

    void setBuffer(final String buffer)
    {
        this.buffer = new CharSequenceInputBuffer(buffer);
    }

    public InputBuffer getBuffer()
    {
        return buffer;
    }

    void setHasMatch(final boolean hasMatch)
    {
        this.hasMatch = hasMatch;
    }

    void setParseTree(final NodeVerifier<V> parseTree)
    {
        this.parseTree = parseTree;
    }

    void setErrors(final List<ParseErrorVerifier<?>> errors)
    {
        this.errors.addAll(errors);
    }

    @Override
    public void verify(@Nonnull final SoftAssertions soft,
        @Nonnull final ParsingResult<V> toVerify)
    {
        soft.assertThat(toVerify.isSuccess()).as("rule matches/does not match")
            .isEqualTo(hasMatch);
        parseTree.setBuffer(buffer).verify(soft, toVerify.getParseTree());
        soft.assertThat(toVerify.getParseErrors().size())
            .as("number of recorded errors").isEqualTo(errors.size());
        final int size = Math.min(toVerify.getParseErrors().size(),
            errors.size());
        for (int index = 0; index < size; index++)
            errors.get(index).verify(soft,
                toVerify.getParseErrors().get(index));
    }
}
