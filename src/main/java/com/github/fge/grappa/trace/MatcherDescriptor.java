package com.github.fge.grappa.trace;

import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.matchers.base.Matcher;

public final class MatcherDescriptor
{
    private final int id;
    private final String className;
    private final MatcherType type;
    private final String name;

    public MatcherDescriptor(final int id, final Matcher matcher)
    {
        this.id = id;
        className = matcher.getClass().getSimpleName();
        type = matcher.getType();
        name = matcher.getLabel();
    }

    // To be used on reading
    public MatcherDescriptor(final int id, final String className,
        final MatcherType type, final String name)
    {
        this.id = id;
        this.className = className;
        this.type = type;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getClassName()
    {
        return className;
    }

    public MatcherType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }
}
