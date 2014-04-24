package org.parboiled.matchers;

import org.mockito.InOrder;
import org.parboiled.MatcherContext;
import org.parboiled.buffers.DefaultInputBuffer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class SupplementaryCharMatcherTest
{
    private static final int PILE_OF_POO = 0x1f4a9;
    private static final int DROPLET = 0x1f4a7;
    private static final char[] POO_CHARS = Character.toChars(PILE_OF_POO);

    private MatcherContext<?> context;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void initContext()
    {
        final DefaultInputBuffer buffer = new DefaultInputBuffer(POO_CHARS);
        context = mock(MatcherContext.class);
        when(context.getInputBuffer()).thenReturn(buffer);
    }

    @Test
    public void whenMatchedContextIsCorrectlyManipulated()
    {
        final Matcher matcher = new SupplementaryCharMatcher(PILE_OF_POO);
        assertTrue(matcher.match(context));
        final InOrder inOrder = inOrder(context);
        inOrder.verify(context).advanceIndex(2);
        inOrder.verify(context).createNode();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void whenNotMatchedContextIsCorrectlyManipulated()
    {
        final Matcher matcher = new SupplementaryCharMatcher(DROPLET);
        assertFalse(matcher.match(context));
        verify(context, never()).advanceIndex(anyInt());
        verify(context, never()).createNode();
    }
}