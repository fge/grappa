package com.github.fge.grappa.illegal;

public final class IllegalZeroOrMoreTest
    extends IllegalGrammarTest
{
    public IllegalZeroOrMoreTest()
    {
        super(IllegalZeroOrMoreParser.class,
            "the inner rule of zeroOrMore() cannot match an empty input");
    }
}
