package com.github.fge.grappa.trace.parser;

import com.github.fge.grappa.trace.TraceEvent;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TraceEventCollector
{
    private final List<TraceEvent> events = new ArrayList<>();

    @Subscribe
    public void receiveEvent(final TraceEvent event)
    {
        events.add(event);
    }

    public List<TraceEvent> getEvents()
    {
        return Collections.unmodifiableList(events);
    }
}
