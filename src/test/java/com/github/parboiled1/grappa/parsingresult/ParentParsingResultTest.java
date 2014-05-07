package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.ParentParser;

import java.io.IOException;

public final class ParentParsingResultTest
    extends ParsingResultTest<ParentParser, Object>
{
    public ParentParsingResultTest()
        throws IOException
    {
        super(ParentParser.class, "parent.json");
    }
}
