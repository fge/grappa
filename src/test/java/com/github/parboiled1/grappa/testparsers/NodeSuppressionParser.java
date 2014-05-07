package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

public class NodeSuppressionParser
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
        return sequence(ef(), gh());
    }

    public Rule ab()
    {
        return sequence(a(), b());
    }

    @SuppressSubnodes
    public Rule cd()
    {
        return sequence(c(), d());
    }

    public Rule ef()
    {
        return sequence(e(), f());
    }

    public Rule gh()
    {
        return sequence(g(), h()).suppressNode();
    }

    public Rule a()
    {
        return ch('a');
    }

    @SuppressNode
    public Rule b()
    {
        return ch('b');
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
        return ch('e').suppressSubnodes();
    }

    public Rule f()
    {
        return ch('f').suppressNode();
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
