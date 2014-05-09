package com.github.parboiled1.grappa.misc;

import com.google.common.io.CharSink;
import org.parboiled.common.Sink;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public final class SystemOutCharSource
    extends CharSink
    implements Sink<CharSequence>
{
    public static final CharSink INSTANCE = new SystemOutCharSource();

    private SystemOutCharSource()
    {
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
        return new OutputStreamWriter(System.out);
    }

    @Override
    public void receive(final CharSequence value)
    {
        try {
            write(value);
        } catch (IOException e) {
            throw new RuntimeException("cannot write to sink", e);
        }
    }
}
