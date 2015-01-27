package com.github.fge.grappa.illegal;

public final class IllegalSequenceTest
    extends IllegalGrammarTest
{
    public IllegalSequenceTest()
    {
        super(IllegalSequenceParser.class,
            "the first rule of a sequence() cannot be an action");
    }
}
