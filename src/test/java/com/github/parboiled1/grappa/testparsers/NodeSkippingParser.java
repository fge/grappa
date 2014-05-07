package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.SkipNode;

public class NodeSkippingParser
    extends TestParser<Object>
{
    @Override
    @Label("abcdefgh")
    public Rule mainRule()
    {
        return sequence(abcd(), efgh());
    }

    public Rule abcd()
    {
        return sequence(ab(), cd());
    }

    public Rule efgh()
    {
        return sequence(ef(), gh()).skipNode();
    }

    public Rule ab()
    {
        return sequence(a(), b());
    }

    @SkipNode
    public Rule cd()
    {
        return sequence(c(), d());
    }

    public Rule ef()
    {
        return sequence(e(), f());
    }

    @SkipNode
    public Rule gh()
    {
        return sequence(g(), h()).skipNode();
    }

    public Rule a()
    {
        return ch('a');
    }

    public Rule b()
    {
        return ch('b').skipNode();
    }

    public Rule c()
    {
        return ch('c');
    }

    public Rule d()
    {
        return ch('d');
    }

    public Rule e()
    {
        return ch('e');
    }

    public Rule f()
    {
        return ch('f');
    }

    public Rule g()
    {
        return ch('g');
    }

    public Rule h()
    {
        return ch('h');
    }
}
