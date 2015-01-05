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

import com.google.common.io.CharSink;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * A {@link CharSink} over {@code System.out}
 */
// TODO: only one user (TracingParseRunner)
public final class SystemOutCharSink
    extends CharSink
{
    public static final CharSink INSTANCE = new SystemOutCharSink();

    private SystemOutCharSink()
    {
    }

    /**
     * Opens a new {@link Writer} for writing to this sink. This method
     * should return a new, independent writer each time it is called.
     *
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
        return SystemOutWriter.INSTANCE;
    }

    /**
     * Writer returned by {@link CharSink#openStream()}
     *
     * <p>It appears that this method opens a new writer for <em>each</em> of
     * its operations; not sure whether this is a bug but on the other hand the
     * base class does not implement {@link Closeable}, so maybe that explains
     * part of it. But not all.</p>
     *
     * <p>This is therefore the "new, independent writer" returned by this
     * method which is in fact always the same; it just delegates to {@code
     * System.out} and only overwrites the base method which all {@code Writer}s
     * must implement.</p>
     */
    private static final class SystemOutWriter
        extends Writer
    {
        @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
        private static final Writer INSTANCE = new SystemOutWriter();

        @Override
        public void write(@Nonnull final char[] cbuf, final int off,
            final int len)
            throws IOException
        {
            System.out.append(CharBuffer.wrap(cbuf, off, len));
        }

        @Override
        public void flush()
            throws IOException
        {
            System.out.flush();
        }

        @Override
        public void close()
            throws IOException
        {
            // Nothing
        }
    }
}
