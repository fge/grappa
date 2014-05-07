package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.RecursiveParser;

import java.io.IOException;

public final class RecursiveParsingResultTest
    extends ParsingResultTest<RecursiveParser, Object>
{
    public RecursiveParsingResultTest()
        throws IOException
    {
        super(RecursiveParser.class, "simple.json");
    }
}
