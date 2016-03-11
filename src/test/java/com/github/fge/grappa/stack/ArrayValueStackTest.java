package com.github.fge.grappa.stack;

import org.assertj.core.api.AutoCloseableSoftAssertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ArrayValueStackTest
    extends ValueStackTest
{
    @Override
    protected ValueStack<Object> newStack()
    {
        final Object[] elements = { o1, o2, o3, o4, o5, o6 };

        return new ArrayValueStack<>(elements);
    }

    @Override
    protected void checkNoContents()
    {
        assertThat(stack.size()).isEqualTo(0);

        final Object[] array = ((ArrayValueStack<Object>) stack).getArray();

        for (final Object o: array)
            assertThat(o).isNull();
    }

    @Override
    protected void checkContents(final Object first, final Object... others)
    {
        final Object[] array = ((ArrayValueStack<Object>) stack).getArray();
        final int length = array.length;
        final int size = 1 + others.length;

        assertThat(stack.size()).isEqualTo(size);

        final List<Object> expected = new ArrayList<>();
        expected.add(first);
        Collections.addAll(expected, others);

        try (
            final AutoCloseableSoftAssertions soft
                = new AutoCloseableSoftAssertions();
        ) {
            for (int index = 0; index < size; index++)
                soft.assertThat(array[index])
                    .as("element at index %d", index)
                    .isEqualTo(expected.get(index));
            for (int index = size; index < length; index++)
                soft.assertThat(array[index])
                    .as("null element at index %d?", index)
                    .isNull();
        }
    }
}

