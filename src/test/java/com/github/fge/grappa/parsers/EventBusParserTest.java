package com.github.fge.grappa.parsers;

import com.github.fge.grappa.Grappa;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.ListeningParseRunner;
import com.google.common.eventbus.Subscribe;
import org.testng.annotations.Test;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EventBusParserTest
{
    protected static class MyParser
        extends EventBusParser<Void>
    {
        public Rule theRule()
        {
            return sequence(empty(), post(match()));
        }
    }

    private static class MyListener
    {
        @Subscribe
        public void triggered(final String ignored)
        {
        }
    }

    @Test
    public void busPostTest()
    {
        final MyParser parser = Grappa.createParser(MyParser.class);
        final MyListener listener = spy(new MyListener());
        parser.register(listener);

        final ListeningParseRunner<Void> runner
            = new ListeningParseRunner<>(parser.theRule());

        runner.run("");

        verify(listener, only()).triggered("");
    }

}
