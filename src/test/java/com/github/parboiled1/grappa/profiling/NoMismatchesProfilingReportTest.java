package com.github.parboiled1.grappa.profiling;

import com.github.parboiled1.grappa.testparsers.NoMismatchesParser;

import java.io.IOException;

public final class NoMismatchesProfilingReportTest
    extends ProfilingReportTest<NoMismatchesParser, Integer>
{
    public NoMismatchesProfilingReportTest()
        throws IOException
    {
        super(NoMismatchesParser.class, "noMismatches.json", "2");
    }
}
