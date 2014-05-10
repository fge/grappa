package com.github.parboiled1.grappa.testparsers;

import org.parboiled.JoinParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
public abstract class TestParser<V>
    extends JoinParser<V>
{
    public abstract Rule mainRule();
}
