package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;

public class RecursiveParser
    extends TestParser<Object>
{
    @Override
    @DontLabel
    public Rule mainRule()
    {
        return sequence(ignoreCase('a'), optional(mainRule()));
    }
}
