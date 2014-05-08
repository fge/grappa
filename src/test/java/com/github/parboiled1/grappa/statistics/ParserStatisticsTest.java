package com.github.parboiled1.grappa.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.assertions.verify.ParserStatisticsVerifier;
import com.github.parboiled1.grappa.testparsers.TestParser;
import com.google.common.io.Closer;
import org.assertj.core.api.SoftAssertions;
import org.parboiled.Parboiled;
import org.parboiled.ParserStatistics;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

@Test
public abstract class ParserStatisticsTest<P extends TestParser<V>, V>
{
    private static final String RESOURCE_PREFIX = "/statistics/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ParserStatisticsVerifier data;
    private final ParserStatistics stats;

    protected ParserStatisticsTest(final Class<P> c, final String resourceName)
        throws IOException
    {
        final String path = RESOURCE_PREFIX + resourceName;
        final Closer closer = Closer.create();
        final InputStream in;

        try {
            in = closer.register(ParserStatisticsTest.class
                .getResourceAsStream(path));
            if (in == null)
                throw new IOException("resource " + path + " not found");
            data = MAPPER.readValue(in, ParserStatisticsVerifier.class);
        } finally {
            closer.close();
        }

        final TestParser<V> parser = Parboiled.createParser(c);
        stats = ParserStatistics.generateFor(parser.mainRule());
    }

    @Test
    public final void statsAreWhatIsPlanned()
    {
        final SoftAssertions soft = new SoftAssertions();
        data.verify(soft, stats);
        soft.assertAll();
    }
}
