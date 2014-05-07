package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.NodeSuppressionParser;

import java.io.IOException;

public final class NodeSuppressionParsingResultTest
    extends ParsingResultTest<NodeSuppressionParser, Object>
{
    public NodeSuppressionParsingResultTest()
        throws IOException
    {
        super(NodeSuppressionParser.class, "nodeSuppression.json");
    }
}
