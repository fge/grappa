package com.github.parboiled1.grappa.assertions;

import org.assertj.core.api.SoftAssertions;
import org.parboiled.errors.InvalidInputError;

public final class InvalidInputErrorVerifier
    extends ParseErrorVerifier<InvalidInputError>
{
    InvalidInputErrorVerifier()
    {
        super(InvalidInputError.class);
    }

    @Override
    protected void verifyDetails(final SoftAssertions soft,
        final InvalidInputError toVerify)
    {

    }
}
