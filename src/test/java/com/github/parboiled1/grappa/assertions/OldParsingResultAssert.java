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

    public OldParsingResultAssert<V> hasNoErrors()
    {
        assertThat(actual.hasErrors()).overridingErrorMessage(
            "parsing result should not have any errors!"
        ).isFalse();
        return this;
    }

    public OldParsingResultAssert<V> hasStack(@Nonnull final V... values)
    {
        final List<V> list = Lists.newArrayList(values);
        Collections.reverse(list);
        final List<V> stack
            = Lists.newArrayList(actual.valueStack);
        assertThat(stack).overridingErrorMessage(
            "stack does not have the expected values!\nExpected: %s\n"
            + "Actual  : %s", list, stack
        ).containsExactlyElementsOf(list);
        return this;
    }
}
