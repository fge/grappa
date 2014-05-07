package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.PrevCallsParser;

import java.io.IOException;

public final class PrevCallsParsingResultTest
    extends ParsingResultTest<PrevCallsParser, Integer>
{
    public PrevCallsParsingResultTest()
        throws IOException
    {
        super(PrevCallsParser.class, "prevCalls.json");
    }
}
