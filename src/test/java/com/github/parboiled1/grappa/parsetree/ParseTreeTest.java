package com.github.parboiled1.grappa.parsetree;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.TestParser;
import com.github.parboiled1.grappa.assertions.ParseTreeData;
import com.google.common.io.Closer;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

@Test
public abstract class ParseTreeTest
{
    private static final String RESOURCE_PREFIX = "/parseTrees/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TypeReference<ParseTreeData<Object>> typeRef
        = new TypeReference<ParseTreeData<Object>>() {};

    private final Node<Object> tree;
    private final ParseTreeData<Object> data;

    protected ParseTreeTest(final Class<? extends TestParser> c,
        final String resourceName, final String input)
        throws IOException
    {
        final TestParser parser = Parboiled.createParser(c);
        final ParseRunner<Object> runner
            = new ReportingParseRunner<Object>(parser.mainRule());
        final ParsingResult<Object> result = runner.run(input);
        tree = result.parseTreeRoot;

        final String path = RESOURCE_PREFIX + resourceName;
        final Closer closer = Closer.create();
        final InputStream in;

        try {
            in = closer.register(ParseTreeTest.class
                .getResourceAsStream(path));
            if (in == null)
                throw new IOException("resource " + path + " not found");
            data = MAPPER.readValue(in, typeRef);
            data.setBuffer(result.inputBuffer);
        } finally {
            closer.close();
        }
    }

    @Test
    public final void treeIsWhatIsExpected()
    {
        final SoftAssertions soft = new SoftAssertions();
        data.verify(soft, tree);
        soft.assertAll();
    }
}
