/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.parboiled1.grappa.misc;

import com.github.parboiled1.grappa.annotations.WillBeRemoved;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSink;
import org.parboiled.annotations.ForBackwardsCompatibilityOnly;
import org.parboiled.common.Sink;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Backwards compatibility class
 *
 * <p>Will be removed when {@link Sink} is removed; this class is here to
 * provide a {@link Writer} for a {@link CharSink}.</p>
 *
 * @see CharSink#openStream()
 */
@ForBackwardsCompatibilityOnly
@WillBeRemoved(version = "1.1")
public final class SinkWriter
    extends Writer
    implements Sink<String>
{
    private final Sink<String> sink;

    public SinkWriter(@Nonnull final Sink<String> sink)
    {
        this.sink = Preconditions.checkNotNull(sink);
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
