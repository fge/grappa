package com.github.fge.grappa.illegal;

import com.github.fge.grappa.rules.Rule;

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
