package com.github.parboiled1.grappa.assertions;

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
