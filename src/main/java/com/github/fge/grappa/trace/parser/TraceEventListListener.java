package com.github.fge.grappa.trace.parser;

import com.github.fge.grappa.trace.TraceEvent;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public final class TraceEventListListener
{
    private final List<TraceEvent> events = new ArrayList<>();

    @Subscribe
    public void handleTraceEvent(final TraceEvent event)
    {
        System.out.println(event);
    }
}
