package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.SimpleParser;

import java.io.IOException;

public final class SimpleParsingResultTest
    extends ParsingResultTest<SimpleParser, Object>
{
    public SimpleParsingResultTest()
        throws IOException
    {
        super(SimpleParser.class, "simple.json");
    }
}
