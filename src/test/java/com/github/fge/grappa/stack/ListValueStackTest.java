package com.github.fge.grappa.stack;

import org.assertj.core.api.AutoCloseableSoftAssertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ListValueStackTest
    extends ValueStackTest
{
    @Override
    protected ValueStack<Object> newStack()
    {
        final Collection<Object> values = Arrays.asList(o1, o2, o3, o4, o5, o6);
        return new ListValueStack<>(values);
    }

    @Override
    protected void checkNoContents()
    {
        assertThat(stack.isEmpty()).isTrue();
    }

    @Override
    protected void checkContents(final Object first, final Object... others)
    {
        final List<Object> values = new ArrayList<>();
        values.add(first);
        Collections.addAll(values, others);

        final int expectedSize = 1 + others.length;

        assertThat(stack.size()).isEqualTo(expectedSize);

        try (
            final AutoCloseableSoftAssertions soft
                = new AutoCloseableSoftAssertions();
        ) {
            for (int index = 0; index < expectedSize; index++)
                soft.assertThat(stack.peek(index))
                    .as("element at index %d", index)
                    .isEqualTo(values.get(index));
        }

    }
}
