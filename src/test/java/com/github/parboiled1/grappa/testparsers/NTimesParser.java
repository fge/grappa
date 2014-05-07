package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;

public class NTimesParser
    extends TestParser<Object>
{
    @Override
    public Rule mainRule()
    {
        return nTimes(3, fourDigits(), operator());
    }

    public Rule operator()
    {
        return firstOf('+', '-');
    }

    public Rule fourDigits()
    {
        return nTimes(4, digit());
    }
}
