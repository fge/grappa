package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.NodeSkippingParser;

import java.io.IOException;

public final class NodeSkippingParsingResultTest
    extends ParsingResultTest<NodeSkippingParser, Object>
{
    public NodeSkippingParsingResultTest()
        throws IOException
    {
        super(NodeSkippingParser.class, "nodeSkipping.json");
    }
}
