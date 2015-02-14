package com.github.fge.grappa.run.trace;

import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.matchers.base.Matcher;

final class MatcherDescriptor
{
    private final int id;
    private final String className;
    private final MatcherType type;
    private final String name;

    MatcherDescriptor(final int id, final Matcher matcher)
    {
        this.id = id;
        className = matcher.getClass().getSimpleName();
        type = matcher.getType();
        name = matcher.getLabel();
    }

    int getId()
    {
        return id;
    }

    String getClassName()
    {
        return className;
    }

    MatcherType getType()
    {
        return type;
    }

    String getName()
    {
        return name;
    }
}
