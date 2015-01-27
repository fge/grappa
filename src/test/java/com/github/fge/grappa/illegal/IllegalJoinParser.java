package com.github.fge.grappa.illegal;

import org.parboiled.Rule;

public class IllegalJoinParser
    extends IllegalGrammarParser
{
    @Override
    Rule illegal()
    {
        return join("a").using(empty()).times(1);
    }

    @Override
    Rule legal()
    {
        return join("a").using(nonEmpty()).times(1);
    }
}
