package com.github.parboiled1.grappa.assertions;

import org.parboiled.errors.InvalidInputError;

public final class InvalidInputErrorAssert
    extends ParseErrorAssert<InvalidInputError>
{
    InvalidInputErrorAssert(final InvalidInputError actual,
        final Class<?> selfType)
    {
        super(actual, selfType);
    }
}
