package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

public class SimpleParser
    extends TestParser<Object>
{
    @Override
    public Rule mainRule()
    {
        return sequence(
            digit(),
            operator(),
            digit(),
            anyOf("abcd"),
            oneOrMore(noneOf("abcd")),
            EOI
        );
    }

    public Rule operator()
    {
        return firstOf(ch('+'), '-');
    }
}
