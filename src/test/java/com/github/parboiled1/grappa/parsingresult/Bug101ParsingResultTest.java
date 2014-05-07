package com.github.parboiled1.grappa.parsingresult;

import com.github.parboiled1.grappa.testparsers.Bug101Parser;

import java.io.IOException;

public final class Bug101ParsingResultTest
    extends ParsingResultTest<Bug101Parser, Object>
{
    public Bug101ParsingResultTest()
        throws IOException
    {
        super(Bug101Parser.class, "bug101.json");
    }
}
