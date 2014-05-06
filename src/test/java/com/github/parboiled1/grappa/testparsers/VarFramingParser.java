package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.support.Var;

public class VarFramingParser
    extends TestParser<Integer>
{
    int count = 1;

    @Override
    public Rule mainRule()
    {
        final Var<Integer> a = new Var<Integer>(-1);
        return sequence(
            digits(),
            a.set(peek()),
            someRule(a),
            optional('+', mainRule(), push(a.get()))
        );
    }

    @SuppressNode
    public Rule digits()
    {
        return sequence(
            oneOrMore(digit()),
            push(Integer.parseInt(match()))
        );
    }

    public Rule someRule(final Var<Integer> var)
    {
        return toRule(var.get() == count++);
    }
}
