package com.github.fge.grappa.illegal;

import org.parboiled.Rule;

public class IllegalOneOrMoreParser
    extends IllegalGrammarParser
{
    @Override
    Rule illegal()
    {
        return oneOrMore(empty());
    }

    @Override
    Rule legal()
    {
        return oneOrMore(nonEmpty());
    }
}
