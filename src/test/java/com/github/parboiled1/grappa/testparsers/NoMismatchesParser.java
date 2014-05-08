package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

public class NoMismatchesParser
    extends TestParser<Integer>
{
    @Override
    public Rule mainRule()
    {
        return sequence(firstOf(zero(), one(), two()), EOI);
    }

    Rule zero()
    {
        return sequence(testNot(sevenOrNine()), ch('0'));
    }

    Rule one()
    {
        return sequence(testNot(sevenOrNine()), ch('1'));
    }

    Rule two()
    {
        return sequence(testNot(sevenOrNine()), ch('2'));
    }

    Rule sevenOrNine()
    {
        return firstOf('7', '9');
    }
}
