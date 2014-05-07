package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.Reference;

public class PrevCallsParser
    extends TestParser<Integer>
{
    @Override
    @SuppressSubnodes
    public Rule mainRule()
    {
        final Reference<Integer> a = new Reference<Integer>();
        final Reference<Character> op = new Reference<Character>();
        final Reference<Integer> b = new Reference<Integer>();
        return sequence(
            digits(), a.set(pop()),
            operator(), op.set(matchedChar()),
            digits(), b.set(pop()),
            EOI,
            push(op.get() == '+' ? a.get() + b.get() : a.get() - b.get()));
    }

    public Rule operator()
    {
        return firstOf('+', '-');
    }

    public Rule digits()
    {
        return sequence(digits2(), debug());
    }

    boolean debug()
    {
        return true;
    }

    public Rule digits2()
    {
        return sequence(oneOrMore(digit()), push(Integer.parseInt(match())));
    }
}
