package com.github.parboiled1.grappa.statistics;

import com.github.parboiled1.grappa.testparsers.MemoMismatchesParser;

import java.io.IOException;

public final class MemoMismatchesStatisticsTest
    extends ParserStatisticsTest<MemoMismatchesParser, Integer>
{

    public MemoMismatchesStatisticsTest()
        throws IOException
    {
        super(MemoMismatchesParser.class, "memoMismatches.json");
    }
}