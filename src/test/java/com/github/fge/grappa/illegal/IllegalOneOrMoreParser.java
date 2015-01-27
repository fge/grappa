package com.github.fge.grappa.illegal;

import com.github.fge.grappa.rules.Rule;

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
