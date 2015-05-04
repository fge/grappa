package com.github.fge.grappa.issues.issue26;

import com.github.fge.grappa.annotations.Cached;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.support.Var;

import java.util.concurrent.atomic.AtomicInteger;

public class Issue26Parser
    extends BaseParser<Object>
{
    @Cached
    public Rule rule2(final Var<Integer> count)
    {
        return firstOf(
            sequence("c", count.set(0)),
            sequence("b", rule2(count), count.set(count.getNonnull()+1), "b")
        );
    }

    @Cached
    public Rule rule3(final Var<AtomicInteger> count)
    {
        return firstOf(
            sequence("c", set(count, 0)),
            sequence(
                "b", rule3(count), set(count, count.getNonnull().get()+1), "b"
            )
        );
    }

    public boolean set(final Var<AtomicInteger> var, final int value)
    {
        var.getNonnull().set(value);
        return true;
    }
}
