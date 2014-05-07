package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

public class RecursiveParser
    extends TestParser<Object>
{
    @Override
    public Rule mainRule()
    {
        return sequence(ignoreCase('a'), optional(mainRule()));
    }
}
