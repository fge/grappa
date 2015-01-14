/*
 * Copyright (C) 2015 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.parboiled1.grappa.trace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.run.MatchFailureEvent;
import com.github.parboiled1.grappa.run.MatchSuccessEvent;
import com.github.parboiled1.grappa.run.ParseRunnerListener;
import com.github.parboiled1.grappa.run.PostParseEvent;
import com.github.parboiled1.grappa.run.PreMatchEvent;
import com.github.parboiled1.grappa.run.PreParseEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.WillCloseWhenClosed;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.parboiled1.grappa.trace.TraceEventType.BEFORE_MATCH;
import static com.github.parboiled1.grappa.trace.TraceEventType.MATCH_FAILURE;
import static com.github.parboiled1.grappa.trace.TraceEventType.MATCH_SUCCESS;

@ParametersAreNonnullByDefault
public final class TracingParseRunnerListener<V>
    extends ParseRunnerListener<V>
    implements AutoCloseable
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("create", "true");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int BUFSIZE = 16384;

    @WillCloseWhenClosed
    private final FileSystem zipfs;
    private final List<TraceEvent> events = new ArrayList<>();

    private long startDate;

    public TracingParseRunnerListener(final String zipPath)
        throws IOException
    {
        final Path path = Paths.get(zipPath);
        final String uri = "jar:" + path.toUri().toString();
        zipfs = FileSystems.newFileSystem(URI.create(uri), ZIPFS_ENV);
    }

    @Override
    public void beforeParse(final PreParseEvent<V> event)
    {
        final InputBuffer buffer = event.getContext().getInputBuffer();
        final Path inputText = zipfs.getPath("/input.txt");
        final int length = buffer.length();

        try (
            final BufferedWriter writer = Files.newBufferedWriter(inputText,
                StandardCharsets.UTF_8);
        ) {
            int index = 0;
            int remaining = length;
            int toWrite;

            while (remaining > 0) {
                toWrite = Math.min(remaining, BUFSIZE);
                writer.append(buffer.extract(index, toWrite));
                index += toWrite;
                remaining -= toWrite;
            }

        } catch (IOException oops) {
            throw new RuntimeException("failed to write input text", oops);
        }
        startDate = System.currentTimeMillis();
    }

    @Override
    public void beforeMatch(final PreMatchEvent<V> event)
    {
        events.add(new TraceEvent(BEFORE_MATCH, event.getContext()));
    }

    @Override
    public void matchSuccess(final MatchSuccessEvent<V> event)
    {
        events.add(new TraceEvent(MATCH_SUCCESS, event.getContext()));
    }

    @Override
    public void matchFailure(final MatchFailureEvent<V> event)
    {
        events.add(new TraceEvent(MATCH_FAILURE, event.getContext()));
    }

    @Override
    public void afterParse(final PostParseEvent<V> event)
    {
        final ParsingRunTrace trace = new ParsingRunTrace(startDate, events);
        final Path path = zipfs.getPath("/trace.json");
        final Path tmpfile;

        //noinspection OverlyBroadCatchBlock
        try {
            tmpfile = Files.createTempFile("trace", "xxx");
            //noinspection NestedTryStatement
            try (
                final OutputStream out = Files.newOutputStream(tmpfile);
            ) {
                MAPPER.writeValue(out, trace);
            }
            Files.move(tmpfile, path);
        } catch (IOException oops) {
            throw new RuntimeException("failed to write trace file", oops);
        }
    }

    @Override
    public void close()
        throws IOException
    {
        zipfs.close();
    }
}
