package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.LabellingParser;

import java.io.IOException;

public final class LabellingParsingResultTest
    extends ParsingResultTest<LabellingParser, Object>
{
    public LabellingParsingResultTest()
        throws IOException
    {
        super(LabellingParser.class, "labelling.json");
    }
}
