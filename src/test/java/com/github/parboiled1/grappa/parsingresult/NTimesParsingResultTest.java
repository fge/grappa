package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.NTimesParser;

import java.io.IOException;

public final class NTimesParsingResultTest
    extends ParsingResultTest<NTimesParser, Object>
{
    public NTimesParsingResultTest()
        throws IOException
    {
        super(NTimesParser.class, "nTimes.json");
    }
}
