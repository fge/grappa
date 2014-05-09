package com.github.parboiled1.grappa.misc;

import com.google.common.base.Preconditions;
import org.parboiled.common.Sink;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

public final class SinkWriter
    extends Writer
    implements Sink<String>
{
    private final Sink<String> sink;

    public SinkWriter(@Nonnull final Sink<String> sink)
    {
        this.sink = sink;
    }

    @Override
    public void receive(final String value)
    {
        sink.receive(value);
    }

    /**
     * Writes a portion of an array of characters.
     *
     * @param cbuf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void write(@Nonnull final char[] cbuf,
        final int off, final int len)
        throws IOException
    {
        Preconditions.checkNotNull(cbuf);
        final String toWrite = CharBuffer.wrap(cbuf, off, len).toString();
        sink.receive(toWrite);
    }

    /**
     * Flushes the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     * <p> If the intended destination of this stream is an abstraction provided
     * by the underlying operating system, for example a file, then flushing the
     * stream guarantees only that bytes previously written to the stream are
     * passed to the operating system for writing; it does not guarantee that
     * they are actually written to a physical device such as a disk drive.
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void flush()
        throws IOException
    {
        // nothing
    }

    /**
     * Closes the stream, flushing it first. Once the stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown. Closing a previously closed stream has no effect.
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void close()
        throws IOException
    {
        // nothing
    }
}
