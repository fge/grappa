package com.github.fge.grappa.stack;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class ArrayValueStackInternalTest
{
    @Test
    public void expandShrinkTest()
    {
        final ArrayValueStack<Object> valueStack = new ArrayValueStack<>();

        Object[] array;
        int expectedLength;

        for (int i = 0; i < ArrayValueStack.INITIAL_SIZE; i++)
            valueStack.push(new Object());

        array = valueStack.getArray();
        expectedLength = ArrayValueStack.INITIAL_SIZE;

        assertThat(array.length).isEqualTo(expectedLength);

        valueStack.push(new Object());

        array = valueStack.getArray();
        expectedLength = ArrayValueStack.INITIAL_SIZE
            + ArrayValueStack.SIZE_INCREASE;

        assertThat(array.length).isEqualTo(expectedLength);

        valueStack.pop();

        array = valueStack.getArray();
        expectedLength = ArrayValueStack.INITIAL_SIZE;

        assertThat(array.length).isEqualTo(expectedLength);
    }
}
