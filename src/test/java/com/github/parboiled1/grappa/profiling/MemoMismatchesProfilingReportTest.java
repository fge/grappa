package com.github.parboiled1.grappa.profiling;

import com.github.parboiled1.grappa.testparsers.MemoMismatchesParser;

import java.io.IOException;

public final class MemoMismatchesProfilingReportTest
    extends ProfilingReportTest<MemoMismatchesParser, Integer>
{
    public MemoMismatchesProfilingReportTest()
        throws IOException
    {
        super(MemoMismatchesParser.class, "memoMismatches.json", "2");
    }
}
