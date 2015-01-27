package com.github.fge.grappa.illegal;

public final class IllegalJoinTest
    extends IllegalGrammarTest
{
    public IllegalJoinTest()
    {
        super(IllegalJoinParser.class,
            "the joining rule of a join() rule cannot match an empty input");
    }
}
