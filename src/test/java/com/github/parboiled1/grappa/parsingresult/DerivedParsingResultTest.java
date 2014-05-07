package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.DerivedParser;

import java.io.IOException;

public final class DerivedParsingResultTest
    extends ParsingResultTest<DerivedParser, Object>
{
    public DerivedParsingResultTest()
        throws IOException
    {
        super(DerivedParser.class, "derived.json");
    }
}
