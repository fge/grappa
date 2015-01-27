package com.github.fge.grappa.illegal;

import org.parboiled.Rule;

public class IllegalZeroOrMoreParser
    extends IllegalGrammarParser
{
    @Override
    Rule illegal()
    {
        return zeroOrMore(empty());
    }

    @Override
    Rule legal()
    {
        return zeroOrMore(nonEmpty());
    }
}
