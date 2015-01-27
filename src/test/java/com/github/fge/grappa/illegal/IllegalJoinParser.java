package com.github.fge.grappa.illegal;

import org.parboiled.Rule;

public class IllegalJoinParser
    extends IllegalGrammarParser
{
    @Override
    Rule theRule()
    {
        return join("a").using(EMPTY).times(1);
    }
}
