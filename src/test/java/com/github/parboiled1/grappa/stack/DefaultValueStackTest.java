package com.github.parboiled1.grappa.stack;

import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.errors.GrammarException;
import org.parboiled.support.ValueStack;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public final class DefaultValueStackTest
{
    private ValueStack<Object> stack;

    @BeforeMethod
    public void initStack()
    {
        stack = new DefaultValueStack<Object>();
    }

    @Test
    public void defaultStackIsEmptyAndHasZeroSize()
    {
        assertThat(stack.isEmpty()).as("new stack should be empty").isTrue();
        assertThat(stack.size()).as("new stack should have size 0")
            .isEqualTo(0);
    }

    @Test
    public void cannotPeekPopPokeDupFromEmptyStack()
    {
        try {
            stack.peek();
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("stack is empty");
        }

        try {
            stack.pop();
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("stack is empty");
        }

        try {
            stack.poke(new Object());
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("stack is empty");
        }

        try {
            stack.dup();
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("stack is empty");
        }
    }

    @Test
    public void cannotPushPokeNullValue()
    {
        try {
            stack.push(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }

        try {
            stack.poke(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }

        try {
            stack.pushAll(null, 1, 2);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }

        try {
            stack.pushAll(1, null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }

        try {
            stack.pushAll(1, 2, null, 3);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }

        try {
            stack.pushAll(Arrays.asList("hello", "world", null));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }

        stack.pushAll(1, 2);

        try {
            stack.push(1, null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("null elements are not allowed");
        }
    }

    @Test
    public void singleElementStackPushPeekPopPokeWorks()
    {
        Object element = new Object();
        final SoftAssertions soft = new SoftAssertions();

        stack.push(element);

        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(1);
        soft.assertThat(stack.isEmpty())
            .as("stack with at least one element is not empty").isFalse();
        soft.assertThat(stack.peek()).as("peek() gives last push()ed")
            .isSameAs(element);
        soft.assertThat(stack.peek(0)).as("peek(0) is same as peek()")
            .isSameAs(element);
        soft.assertThat(stack.pop()).as("pop() gives last push()ed")
            .isSameAs(element);
        soft.assertThat(stack.isEmpty())
            .as("one-element stack popped from becomes empty").isTrue();

        stack.push(element);
        element = new Object();
        stack.poke(element);
        soft.assertThat(stack.peek()).as("poke() replaces the first element")
            .isSameAs(element);

        element = new Object();
        stack.poke(0, element);
        soft.assertThat(stack.pop()).as("poke(0) is the same as poke()")
            .isSameAs(element);

        soft.assertAll();
    }

    @Test
    public void multiPushPeekPopPokeDupAndClearWorks()
    {
        final SoftAssertions soft = new SoftAssertions();
        final Integer two = new Integer(2000000000);
        stack.push(1);
        stack.push(two);

        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(2);
        soft.assertThat(stack)
            .as("elements are in the correct order after single element pushes")
            .containsExactly(two, 1);

        stack.dup();
        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(3);
        soft.assertThat(stack)
            .as("elements are in the correct order after dup()")
            .containsExactly(two, two, 1);

        stack.pop();
        stack.pushAll(3, "helo");
        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(4);
        soft.assertThat(stack)
            .as("elements are in the correct order after multi element push")
            .containsExactly(3, "helo", two, 1);

        Object element;

        element = stack.peek(2);
        soft.assertThat(element).as("down-peek() works correctly")
            .isSameAs(two);

        element = stack.pop(2);
        soft.assertThat(element).as("down-pop() works correctly")
            .isSameAs(two);
        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(3);
        soft.assertThat(stack)
            .as("elements are in the correct order after multi element push")
            .containsExactly(3, "helo", 1);

        stack.pushAll(Arrays.asList("harry", "sally"));
        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(5);
        soft.assertThat(stack)
            .as("elements are in the correct order after iterable element push")
            .containsExactly("harry", "sally", 3, "helo", 1);

        element = "meh";
        stack.poke(2, element);
        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(5);
        soft.assertThat(stack)
            .as("elements are in the correct order after element poke")
            .containsExactly("harry", "sally", element, "helo", 1);

        stack.push(5, 'x');
        soft.assertThat(stack.size()).as("stack has the correct size")
            .isEqualTo(6);
        soft.assertThat(stack)
            .as("elements are in the correct order after element poke")
            .containsExactly("harry", "sally", element, "helo", 1, 'x');

        stack.clear();
        soft.assertThat(stack.isEmpty()).as("cleared stack becomes empty")
            .isTrue();

        soft.assertAll();
    }

    @Test
    public void wrongIndicesYieldExpectedExceptions()
    {
        stack.pushAll(1, 2, 3);

        try {
            stack.pop(3);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("not enough elements in stack");
        }

        try {
            stack.poke(3, new Object());
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("not enough elements in stack");
        }

        try {
            stack.peek(3);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("not enough elements in stack");
        }

        // TODO: hack! See interface description
        try {
            stack.swap4();
            failBecauseExceptionWasNotThrown(GrammarException.class);
        } catch (GrammarException e) {
            assertThat(e).hasMessage("not enough elements in stack");
        }

        try {
            stack.push(4, new Object());
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("not enough elements in stack");
        }
    }

    @DataProvider
    public Iterator<Object[]> getSwapData()
    {
        final List<Object[]> list = Lists.newArrayList();

        int n;
        List<Object> l;

        n = 2;
        l = Arrays.<Object>asList(2, 1, 3, 4, 5, 6);
        list.add(new Object[] { n, l });

        n = 3;
        l = Arrays.<Object>asList(3, 2, 1, 4, 5, 6);
        list.add(new Object[] { n, l });

        n = 4;
        l = Arrays.<Object>asList(4, 3, 2, 1, 5, 6);
        list.add(new Object[] { n, l });

        n = 5;
        l = Arrays.<Object>asList(5, 4, 3, 2, 1, 6);
        list.add(new Object[] { n, l });

        n = 6;
        l = Arrays.<Object>asList(6, 5, 4, 3, 2, 1);
        list.add(new Object[] { n, l });

        return list.iterator();
    }

    @Test(dataProvider = "getSwapData")
    public void swappingWorks(final int n, final List<Object> expected)
    {
        final List<Object> orig = Arrays.<Object>asList(1, 2, 3, 4, 5, 6);
        final SoftAssertions soft = new SoftAssertions();

        stack.pushAll(1, 2, 3, 4, 5, 6);

        ((DefaultValueStack<Object>) stack).swap(n);
        soft.assertThat(stack).as("swap of " + n + " works correctly")
            .containsExactlyElementsOf(expected);

        ((DefaultValueStack<Object>) stack).swap(n);
        soft.assertThat(stack)
            .as("double swap of " + n + " gives back the original")
            .containsExactlyElementsOf(orig);

        soft.assertAll();
    }

    @Test
    public void iteratorReturnedByStackDoesNotSupportRemovals()
    {
        stack.push(1);

        try {
            final Iterator<Object> iterator = stack.iterator();
            iterator.next();
            iterator.remove();
            failBecauseExceptionWasNotThrown(
                UnsupportedOperationException.class);
        } catch (UnsupportedOperationException ignored) {
        }
    }

    @Test
    public void snapshotAndRestoreWorksAsExpected()
    {
        final List<Object> orig = Arrays.<Object>asList(1, 2, 3);
        final List<Object> replace = Arrays.<Object>asList(4, 5, 6);
        final SoftAssertions soft = new SoftAssertions();

        stack.pushAll(1, 2, 3);

        final Object snapshot = stack.takeSnapshot();
        final Object poison = Lists.newArrayList();

        try {
            stack.restoreSnapshot(poison);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ignored) {
        }

        stack.clear();
        stack.pushAll(4, 5, 6);

        soft.assertThat(stack)
            .as("stack contents are correct after snapshot plus modifications")
            .containsExactlyElementsOf(replace);

        stack.restoreSnapshot(snapshot);
        soft.assertThat(stack)
            .as("stack contents are completely restored from snapshot")
            .containsExactlyElementsOf(orig);

        soft.assertAll();
    }
}
