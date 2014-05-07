package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

public class DerivedParser
    extends ParentParser
{
    @Override
    public Rule mainRule()
    {
        return sequence(oneOrMore(super.mainRule()), actions.dummy());
    }
}
