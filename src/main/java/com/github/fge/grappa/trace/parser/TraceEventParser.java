package com.github.fge.grappa.trace.parser;

import com.github.fge.grappa.parsers.EventBusParser;
import com.github.fge.grappa.rules.Rule;

public class TraceEventParser
    extends EventBusParser<Object>
{
    private final TraceEventBuilder builder;

    public TraceEventParser(final TraceEventBuilder builder)
    {
        this.builder = builder;
    }

    Rule traceEvent()
    {
        return sequence(
            eventType(), ';',
            index(), ';',
            level(), ';',
            matcher(), ';',
            matcherType(), ';',
            matcherClass(), ';',
            nanoSeconds(), eof()
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
