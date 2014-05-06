package com.github.parboiled1.grappa.statistics;

import com.github.parboiled1.grappa.testparsers.VarFramingParser;

import java.io.IOException;

public final class VarFramingStatisticsTest
    extends ParserStatisticsTest<VarFramingParser, Integer>
{

    public VarFramingStatisticsTest()
        throws IOException
    {
        super(VarFramingParser.class, "varFraming.json");
    }
}