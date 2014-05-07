package com.github.parboiled1.grappa.testparsers;

import org.parboiled.BaseActions;
import org.parboiled.Rule;

public class ParentParser
    extends TestParser<Object>
{
    public static class Actions
        extends BaseActions<Object>
    {
        public boolean dummy()
        {
            return true;
        }
    }

    public final Actions actions = new Actions();

    @Override
    public Rule mainRule()
    {
        return sequence("ab", "cd", actions.dummy())
            .label("abcd");
    }
}
