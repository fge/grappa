package com.github.parboiled1.grappa.parsingresult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.testparsers.TestParser;
import com.github.parboiled1.grappa.assertions.verify.ParsingResultVerifier;
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
public abstract class ParsingResultTest<P extends TestParser<V>, V>
{
    private static final String RESOURCE_PREFIX = "/parseResults/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TypeReference<ParsingResultVerifier<V>> typeRef
        = new TypeReference<ParsingResultVerifier<V>>() {};

    private final ParsingResult<V> result;
    private final ParsingResultVerifier<V> data;

    protected ParsingResultTest(final Class<P> c,
        final String resourceName)
        throws IOException
    {
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

        final TestParser<V> parser = Parboiled.createParser(c);
        final ParseRunner<V> runner
            = new ReportingParseRunner<V>(parser.mainRule());
        result = runner.run(data.getBuffer());
    }

    @Test
    public final void treeIsWhatIsExpected()
    {
        final SoftAssertions soft = new SoftAssertions();
        data.verify(soft, result);
        soft.assertAll();
    }
}
