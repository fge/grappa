package com.github.parboiled1.grappa.statistics;

import com.github.parboiled1.grappa.testparsers.NoMismatchesParser;

import java.io.IOException;

public final class NoMismatchesStatisticsTest
    extends ParserStatisticsTest<NoMismatchesParser, Integer>
{

    public NoMismatchesStatisticsTest()
        throws IOException
    {
        super(NoMismatchesParser.class, "noMismatches.json");
    }
}