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
import com.google.common.io.CharSink;
import org.parboiled.annotations.ForBackwardsCompatibilityOnly;
import org.parboiled.common.Sink;

import java.io.IOException;
import java.io.Writer;

/**
 * Backwards compatibility class
 *
 * <p>Will be removed when {@link Sink} will be removed.</p>
 */
@ForBackwardsCompatibilityOnly
@WillBeRemoved(version = "1.1")
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
