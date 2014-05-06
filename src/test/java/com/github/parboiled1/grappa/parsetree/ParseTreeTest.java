package com.github.parboiled1.grappa.parsetree;

import com.github.parboiled1.grappa.TestParser;
import com.github.parboiled1.grappa.assertions.ParseTreeVerifier;
import com.google.common.base.Optional;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public abstract class ParseTreeTest
{
    private final Node<Object> tree;
    private final ParseTreeVerifier<Object> descriptor;

    protected ParseTreeTest(final Class<? extends TestParser> c,
        final String resourceName, final String input)
        throws IOException
    {
        final TestParser parser = Parboiled.createParser(c);
        final ParseRunner<Object> runner
            = new ReportingParseRunner<Object>(parser.mainRule());
        final ParsingResult<Object> result = runner.run(input);
        tree = result.parseTreeRoot;
        descriptor = ParseTreeVerifier.read(resourceName, result.inputBuffer);
    }

    @Test
    public final void treeIsWhatIsExpected()
    {
        final SoftAssertions soft = new SoftAssertions();
        descriptor.verify(soft, Optional.of(tree));
        soft.assertAll();
    }
}
