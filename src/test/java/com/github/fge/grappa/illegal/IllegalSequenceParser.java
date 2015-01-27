package com.github.fge.grappa.illegal;

import com.github.fge.grappa.rules.Rule;

public class IllegalSequenceParser
    extends IllegalGrammarParser
{
    @Override
    Rule illegal()
    {
        return sequence(true, "true");
    }

    @Override
    Rule legal()
    {
        return sequence("true", true);
    }
}
