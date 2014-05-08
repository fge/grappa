package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.Label;

public class LabellingParser
    extends TestParser<Object>
{
    @Override
    public Rule mainRule()
    {
        return sequence(number().label("a"), operator().label("firstOp"),
            number().label("b"), operator().label("secondOp"), number());
    }

    public Rule operator()
    {
        return firstOf('+', '-');
    }

    @Label("NUmBER")
    public Rule number()
    {
        return oneOrMore(digit());
    }
}
