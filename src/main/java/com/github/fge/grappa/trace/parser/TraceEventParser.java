package com.github.fge.grappa.trace.parser;

import com.github.fge.grappa.parsers.EventBusParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.trace.TraceEvent;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class TraceEventParser
    extends EventBusParser<TraceEvent>
{
    protected final TraceEventBuilder builder = new TraceEventBuilder();
    private final BlockingQueue<TraceEvent> queue;

    public TraceEventParser(final BlockingQueue<TraceEvent> queue)
    {
        this.queue = Objects.requireNonNull(queue);
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
            nanoSeconds(), eof(),
            pushToQueue()
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
            join(oneOrMore(firstOf(noneOf("\\;"), sequence('\\', noneOf(";")))))
                .using("\\;")
                .min(1),
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

    boolean pushToQueue()
    {
        try {
            queue.put(builder.build());
            return true;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
