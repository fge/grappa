package com.github.parboiled1.grappa;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
public abstract class TestParser
    extends BaseParser<Object>
{
    public abstract Rule mainRule();
}
