package com.github.parboiled1.grappa.parseresult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.TestParser;
import com.github.parboiled1.grappa.assertions.ParsingResultVerifier;
import com.google.common.io.Closer;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

@Test
public abstract class ParsingResultTest
{
    private static final String RESOURCE_PREFIX = "/parseResults/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TypeReference<ParsingResultVerifier<Object>> typeRef
        = new TypeReference<ParsingResultVerifier<Object>>() {};

    private final ParsingResult<Object> result;
    private final ParsingResultVerifier<Object> data;

    protected ParsingResultTest(final Class<? extends TestParser> c,
        final String resourceName, final String input)
        throws IOException
    {
        final TestParser parser = Parboiled.createParser(c);
        final ParseRunner<Object> runner
            = new ReportingParseRunner<Object>(parser.mainRule());
        result = runner.run(input);

        final String path = RESOURCE_PREFIX + resourceName;
        final Closer closer = Closer.create();
        final InputStream in;

        try {
            in = closer.register(ParsingResultTest.class
                .getResourceAsStream(path));
            if (in == null)
                throw new IOException("resource " + path + " not found");
            data = MAPPER.readValue(in, typeRef);
        } finally {
            closer.close();
        }
    }

    @Test
    public final void treeIsWhatIsExpected()
    {
        final SoftAssertions soft = new SoftAssertions();
        data.verify(soft, result);
        soft.assertAll();
    }
}
