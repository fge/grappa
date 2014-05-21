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

package com.github.parboiled1.grappa.assertions;

import com.google.common.collect.Lists;
import org.assertj.core.api.AbstractAssert;
import org.parboiled.support.ParsingResult;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class OldParsingResultAssert<V>
    extends AbstractAssert<OldParsingResultAssert<V>, ParsingResult<V>>
{
    private OldParsingResultAssert(final ParsingResult<V> actual)
    {
        super(actual, OldParsingResultAssert.class);
    }

    public static <E> OldParsingResultAssert<E> assertResult(
        final ParsingResult<E> actual)
    {
        return new OldParsingResultAssert<E>(actual);
    }

    public OldParsingResultAssert<V> hasMatch()
    {
        assertThat(actual.isSuccess()).overridingErrorMessage(
            "parsing result should not have any errors!"
        ).isFalse();
        return this;
    }

    public OldParsingResultAssert<V> hasStack(@Nonnull final V... values)
    {
        final List<V> list = Lists.newArrayList(values);
        Collections.reverse(list);
        final List<V> stack
            = Lists.newArrayList(actual.getValueStack());
        assertThat(stack).overridingErrorMessage(
            "stack does not have the expected values!\nExpected: %s\n"
            + "Actual  : %s", list, stack
        ).containsExactlyElementsOf(list);
        return this;
    }
}
