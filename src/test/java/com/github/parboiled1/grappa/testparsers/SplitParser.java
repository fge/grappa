package com.github.parboiled1.grappa.testparsers;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;

public class SplitParser
    extends TestParser<Object>
{
    final Primitives primitives = Parboiled.createParser(Primitives.class);

    public Rule clause() {
        return sequence(
            digit(),
            primitives.operator(),
            primitives.digit(),
            EOI
        );
    }

    @Override
    @DontLabel
    public Rule mainRule()
    {
        return clause();
    }

    @BuildParseTree
    static class Primitives
        extends BaseParser<Object>
    {
        public Rule operator()
        {
            return firstOf('+', '-');
        }
    }
}
