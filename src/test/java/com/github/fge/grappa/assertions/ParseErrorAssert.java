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

package com.github.fge.grappa.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.errors.ParseError;

abstract class ParseErrorAssert<E extends ParseError>
    extends AbstractAssert<ParseErrorAssert<E>, E>
{
    protected ParseErrorAssert(final E actual, final Class<?> selfType)
    {
        super(actual, selfType);
    }

    final void hasStartIndex(final SoftAssertions soft, final int expectedIndex)
    {
        soft.assertThat(expectedIndex).isEqualTo(actual.getStartIndex());
    }

    final void hasEndIndex(final SoftAssertions soft, final int expectedIndex)
    {
        soft.assertThat(expectedIndex).isEqualTo(actual.getEndIndex());
    }
}
