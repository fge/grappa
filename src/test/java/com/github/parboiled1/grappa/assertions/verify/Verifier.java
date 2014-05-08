package com.github.parboiled1.grappa.assertions.verify;

import org.assertj.core.api.SoftAssertions;

import javax.annotation.Nonnull;

public interface Verifier<T>
{
    void verify(@Nonnull final SoftAssertions soft,
        @Nonnull final T toVerify);
}
