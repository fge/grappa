package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.SplitParser;

import java.io.IOException;

public final class SplitParsingResultTest
    extends ParsingResultTest<SplitParser, Object>
{
    public SplitParsingResultTest()
        throws IOException
    {
        super(SplitParser.class, "split.json");
    }
}
