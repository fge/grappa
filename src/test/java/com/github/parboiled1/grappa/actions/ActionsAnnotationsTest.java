package com.github.parboiled1.grappa.actions;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.SkipActionsInPredicates;
import org.parboiled.parserunners.BasicParseRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class ActionsAnnotationsTest
{
    static class Parser
        extends BaseParser<Object>
    {
        protected final Dummy dummy;

        Parser(final Dummy dummy)
        {
            this.dummy = dummy;
        }

        Rule notSkipped()
        {
            return sequence(EMPTY, dummy.dummy());
        }

        @SkipActionsInPredicates
        Rule skipped()
        {
            return sequence(EMPTY, dummy.dummy());
        }

        Rule rule1()
        {
            return sequence(test(notSkipped()), ANY);
        }

        Rule rule2()
        {
            return sequence(test(skipped()), ANY);
        }
    }

    private interface Dummy
    {
        boolean dummy();
    }

    private Dummy dummy;

    @BeforeMethod
    public void initParser()
    {
        dummy = mock(Dummy.class);
        when(dummy.dummy()).thenReturn(true);
    }

    @Test
    public void byDefaultActionsRunInPredicates()
    {
        final Parser parser
            = Parboiled.createParser(Parser.class, dummy);
        new BasicParseRunner<Object>(parser.rule1()).run("f");
        verify(dummy).dummy();
    }

    @Test
    public void whenAnnotatedActionsDoNotRunInPredicates()
    {
        final Parser parser
            = Parboiled.createParser(Parser.class, dummy);
        new BasicParseRunner<Object>(parser.rule2()).run("f");
        verify(dummy, never()).dummy();
    }
}
