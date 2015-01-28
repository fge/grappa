package com.github.fge.grappa.trace.parser;

import com.github.fge.grappa.parsers.EventBusParser;
import com.github.fge.grappa.rules.Rule;
import com.google.common.collect.Range;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TraceEventParser
    extends EventBusParser<Object>
{
    protected final TraceEventBuilder builder = new TraceEventBuilder();
    protected final Range<Integer> range;

    public TraceEventParser()
    {
        this(Range.atLeast(0));
    }

    public TraceEventParser(final Integer maxEvents)
    {
        if (maxEvents < 0)
            throw new IllegalArgumentException("maxEvents must be positive");
        range = Range.atMost(maxEvents);
    }

    public TraceEventParser(@Nonnull final Range<Integer> range)
    {
        this.range = Objects.requireNonNull(range);
    }

    public Rule traceEvents()
    {
        return join(traceEvent()).using('\n').range(range);
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
            nanoSeconds(),
            post(builder)
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
