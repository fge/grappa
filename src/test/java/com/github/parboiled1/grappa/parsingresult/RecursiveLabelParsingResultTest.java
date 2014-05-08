package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.RecursiveLabelParser;

import java.io.IOException;

public final class RecursiveLabelParsingResultTest
    extends ParsingResultTest<RecursiveLabelParser, Object>
{
    public RecursiveLabelParsingResultTest()
        throws IOException
    {
        super(RecursiveLabelParser.class, "recursiveLabel.json");
    }
}
