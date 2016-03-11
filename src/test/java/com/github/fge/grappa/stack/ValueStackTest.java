package com.github.fge.grappa.stack;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public abstract class ValueStackTest
{
    protected final Object o1 = new Object();
    protected final Object o2 = new Object();
    protected final Object o3 = new Object();
    protected final Object o4 = new Object();
    protected final Object o5 = new Object();
    protected final Object o6 = new Object();

    protected ValueStack<Object> stack;

    @BeforeMethod
    public void createStack()
    {
        stack = newStack();
    }

    protected abstract ValueStack<Object> newStack();

    protected abstract void checkNoContents();

    protected abstract void checkContents(Object first, Object... others);

    @Test
    public final void initialSizeTest()
    {
        assertThat(stack.size()).isEqualTo(6);
    }

    @Test
    public final void iteratorTest()
    {
        assertThat(stack.iterator())
            .containsExactly(o1, o2, o3, o4, o5, o6);
    }

    @Test
    public final void clearTest()
    {
        stack.clear();
        checkNoContents();
    }

    @Test
    public final void pushTest()
    {
        final Object o7 = new Object();
        stack.push(o7);
        checkContents(o7, o1, o2, o3, o4, o5, o6);
    }

    @Test
    public final void pushMiddleTest()
    {
        final Object o7 = new Object();
        stack.push(3, o7);
        checkContents(o1, o2, o3, o7, o4, o5, o6);
    }

    @Test
    public final void pushAtEndTest()
    {
        final Object o7 = new Object();
        stack.push(6, o7);
        checkContents(o1, o2, o3, o4, o5, o6, o7);
    }

    @Test
    public final void popTest()
    {
        final Object expected = o1;
        final Object actual = stack.pop();

        assertThat(expected).isSameAs(actual);
        checkContents(o2, o3, o4, o5, o6);
    }

    @Test
    public final void popMidleTest()
    {
        final Object expected = o5;
        final Object actual = stack.pop(4);

        assertThat(expected).isSameAs(actual);
        checkContents(o1, o2, o3, o4, o6);
    }

    @Test
    public final void peekTest()
    {
        final Object expected = o1;
        final Object actual = stack.peek();

        assertThat(expected).isSameAs(actual);
        checkContents(o1, o2, o3, o4, o5, o6);
    }

    @Test
    public final void peekMiddleTest()
    {
        final Object expected = o3;
        final Object actual = stack.peek(2);

        assertThat(expected).isSameAs(actual);
        checkContents(o1, o2, o3, o4, o5, o6);
    }

    @Test(dependsOnMethods = "peekTest")
    public final void pokeTest()
    {
        final Object expected = new Object();

        stack.poke(expected);

        final Object actual = stack.peek();

        assertThat(actual).isSameAs(expected);
        checkContents(expected, o2, o3, o4, o5, o6);
    }

    @Test(dependsOnMethods = "peekMiddleTest")
    public final void pokeMiddleTest()
    {
        final Object expected = new Object();

        stack.poke(2, expected);

        final Object actual = stack.peek(2);

        assertThat(actual).isSameAs(expected);
        checkContents(o1, o2, expected, o4, o5, o6);
    }

    @Test
    public final void dupTest()
    {
        stack.dup();

        checkContents(o1, o1, o2, o3, o4, o5, o6);
    }

    @Test
    public final void swapTest()
    {
        stack.swap();

        checkContents(o2, o1, o3, o4, o5, o6);
    }

    @Test
    public final void swapMiddleEvenTest()
    {
        stack.swap(4);

        checkContents(o4, o3, o2, o1, o5, o6);
    }

    @Test
    public final void swapMiddleOddTest()
    {
        stack.swap(5);

        checkContents(o5, o4, o3, o2, o1, o6);
    }

    @Test(dependsOnMethods = {
        "pushTest", "dupTest", "pokeTest", "swapTest", "pokeTest"
    })
    public final void snapshotTest()
    {
        final Object snapshot = stack.takeSnapshot();

        final Object value = new Object();

        stack.pop();
        stack.push(value);
        stack.dup();
        stack.swap(3);

        stack.restoreSnapshot(snapshot);

        checkContents(o1, o2, o3, o4, o5, o6);
    }
}
