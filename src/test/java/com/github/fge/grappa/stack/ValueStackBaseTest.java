package com.github.fge.grappa.stack;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class ValueStackBaseTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private ValueStackBase<Object> stack;

    @BeforeMethod
    public void initStack()
    {
        stack = spy(new DummyValueStack());
    }

    @Test
    public void zeroSizeEmptyTest()
    {
        // This is the default; but let's just clarify
        doReturn(0).when(stack).size();

        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    public void nonZeroSizeEmptyTest()
    {
        doReturn(1).when(stack).size();

        assertThat(stack.isEmpty()).isFalse();
    }

    @Test
    public void pushNegativeIndex()
    {
        try {
            stack.push(-1, new Object());
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage())
                .isEqualTo(ValueStackBase.NEGATIVE_INDEX);
        }

        verify(stack, never()).doPush(anyInt(), any());
    }

    @Test
    public void pushIllegalIndex()
    {
        doReturn(2).when(stack).size();

        try {
            stack.push(3, new Object());
            shouldHaveThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage(ValueStackBase.NOT_ENOUGH_ELEMENTS);
        }

        verify(stack, never()).doPush(anyInt(), any());
    }

    @Test
    public void pushTest()
    {
        doReturn(3).when(stack).size();

        final Object element = new Object();

        stack.push(3, element);

        verify(stack).doPush(eq(3), same(element));
    }

    @Test
    public void popNegativeIndex()
    {
        try {
            stack.pop(-1);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(ValueStackBase.NEGATIVE_INDEX);
        }
    }

    @Test
    public void popIllegalIndex()
    {
        doReturn(1).when(stack).size();

        try {
            stack.pop(1);
            shouldHaveThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage(ValueStackBase.NOT_ENOUGH_ELEMENTS);
        }
    }

    @Test
    public void popTest()
    {
        doReturn(3).when(stack).size();

        stack.pop(2);

        verify(stack).doPop(2);
    }

    @Test
    public void peekNegativeIndex()
    {
        try {
            stack.peek(-1);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(ValueStackBase.NEGATIVE_INDEX);
        }
    }

    @Test
    public void peekIllegalIndex()
    {
        doReturn(2).when(stack).size();

        try {
            stack.peek(2);
            shouldHaveThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage(ValueStackBase.NOT_ENOUGH_ELEMENTS);
        }
    }

    @Test
    public void peekTest()
    {
        doReturn(3).when(stack).size();

        final Object expected = new Object();
        doReturn(expected).when(stack).doPeek(2);

        final Object actual = stack.peek(2);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    public void pokeNegativeIndex()
    {
        try {
            stack.poke(-1, new Object());
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(ValueStackBase.NEGATIVE_INDEX);
        }
    }

    @Test
    public void pokeIllegalIndex()
    {
        doReturn(2).when(stack).size();

        try {
            stack.poke(2, new Object());
            shouldHaveThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage(ValueStackBase.NOT_ENOUGH_ELEMENTS);
        }
    }

    @Test
    public void pokeTest()
    {
        doReturn(3).when(stack).size();

        final Object element = new Object();

        stack.poke(2, element);

        verify(stack).doPoke(eq(2), same(element));
    }

}
