package com.github.fge.grappa.illegal;

public final class IllegalOneOrMoreTest
    extends IllegalGrammarTest
{
    public IllegalOneOrMoreTest()
    {
        super(IllegalOneOrMoreParser.class,
            "the inner rule of oneOrMore() cannot match an empty input");
    }
}
