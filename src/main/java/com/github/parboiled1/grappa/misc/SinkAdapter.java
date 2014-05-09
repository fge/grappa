package com.github.parboiled1.grappa.misc;

import com.google.common.io.CharSink;
import org.parboiled.common.Sink;

import java.io.IOException;
import java.io.Writer;

public final class SinkAdapter
    extends CharSink
    implements Sink<String>
{
    private final SinkWriter sinkWriter;

    public SinkAdapter(final Sink<String> sink)
    {
        sinkWriter = new SinkWriter(sink);
    }

    /**
     * Opens a new {@link Writer} for writing to this sink. This method
     * should return a new,
     * independent writer each time it is called.
     * <p>The caller is responsible for ensuring that the returned writer is
     * closed.
     *
     * @throws IOException if an I/O error occurs in the process of opening
     * the writer
     */
    @Override
    public Writer openStream()
        throws IOException
    {
        return sinkWriter;
    }

    @Override
    public void receive(final String value)
    {
        sinkWriter.receive(value);
    }
}
