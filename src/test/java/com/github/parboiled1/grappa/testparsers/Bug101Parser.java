package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

// TODO: find bug reference
public class Bug101Parser
    extends TestParser<Object>
{
    @Override
    public Rule mainRule()
    {
        return firstOf(sequence("a", "c").skipNode(), "a");
    }
}
