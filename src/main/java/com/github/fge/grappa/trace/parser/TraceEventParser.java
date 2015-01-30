package com.github.fge.grappa.trace.parser;

import com.github.fge.grappa.parsers.EventBusParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.trace.TraceEvent;

import java.util.Objects;

public class TraceEventParser
    extends EventBusParser<TraceEvent>
{
    protected final TraceEventBuilder builder;

    public TraceEventParser(final TraceEventBuilder builder)
    {
        this.builder = Objects.requireNonNull(builder);
    }

    public Rule traceEvent()
    {
        return sequence(
            eventType(), ';',
            index(), ';',
            level(), ';',
            matcher(), ';',
            matcherType(), ';',
            matcherClass(), ';',
            nanoSeconds(),
            EOI
        );
    }

    Rule eventType()
    {
        return sequence(digit(), builder.setType(match()));
    }

    Rule index()
    {
        return sequence(oneOrMore(digit()), builder.setIndex(match()));
    }

    Rule level()
    {
        return sequence(oneOrMore(digit()), builder.setLevel(match()));
    }

    Rule matcher()
    {
        return sequence(
            join(oneOrMore(noneOf("\\;"))).using("\\;").min(1),
            builder.setMatcher(match())
        );
    }

    Rule matcherType()
    {
        return sequence(digit(), builder.setMatcherType(match()));
    }

    Rule matcherClass()
    {
        return sequence(oneOrMore(noneOf(";")),
            builder.setMatcherClass(match()));
    }

    Rule nanoSeconds()
    {
        return sequence(oneOrMore(digit()), builder.setNanoseconds(match()));
    }
}
