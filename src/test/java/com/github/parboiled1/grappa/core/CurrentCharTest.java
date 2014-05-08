package com.github.parboiled1.grappa.core;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.BasicParseRunner;
import org.testng.annotations.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class CurrentCharTest
{
    static class Dummy
    {
        boolean store(final char ignore)
        {
            return true;
        }
    }

    static class Parser
        extends BaseParser<Object>
    {
        protected final Dummy dummy;

        Parser(final Dummy dummy)
        {
            this.dummy = dummy;
        }

        Rule rule()
        {
            return sequence(dummy.store(currentChar()), EOI);
        }
    }

    @Test
    public void currentCharWorks()
    {
        final Dummy dummy = spy(new Dummy());
        final Parser parser = Parboiled.createParser(Parser.class, dummy);
        new BasicParseRunner<Object>(parser.rule()).run("a");
        verify(dummy).store('a');
    }
}
