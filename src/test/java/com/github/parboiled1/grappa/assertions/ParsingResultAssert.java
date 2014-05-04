package com.github.parboiled1.grappa.assertions;

import org.assertj.core.api.AbstractAssert;
import org.parboiled.support.ParsingResult;

import static org.assertj.core.api.Assertions.assertThat;

public final class ParsingResultAssert<V>
    extends AbstractAssert<ParsingResultAssert<V>, ParsingResult<V>>
{
    private ParsingResultAssert(final ParsingResult<V> actual)
    {
        super(actual, ParsingResultAssert.class);
    }

    public static <E> ParsingResultAssert<E> assertResult(
        final ParsingResult<E> actual)
    {
        return new ParsingResultAssert<E>(actual);
    }

    public ParsingResultAssert<V> hasNoErrors()
    {
        assertThat(actual.hasErrors()).overridingErrorMessage(
            "parsing result should not have any errors!"
        ).isFalse();
        return this;
    }
}
