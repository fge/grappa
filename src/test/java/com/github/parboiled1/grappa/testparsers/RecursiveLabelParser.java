package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

public class RecursiveLabelParser
    extends TestParser<Object>
{
    @Override
    public Rule mainRule()
    {
        return firstOf(
            'a',
            sequence(
                'b',
                mainRule().label("first"),
                mainRule().label("second")
            )
        );
    }

}
