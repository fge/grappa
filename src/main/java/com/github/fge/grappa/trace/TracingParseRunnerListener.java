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

package com.github.fge.grappa.trace;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.run.MatchFailureEvent;
import com.github.fge.grappa.run.MatchSuccessEvent;
import com.github.fge.grappa.run.ParseRunnerListener;
import com.github.fge.grappa.run.PostParseEvent;
import com.github.fge.grappa.run.PreMatchEvent;
import com.github.fge.grappa.run.PreParseEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public final class TracingParseRunnerListener<V>
    extends ParseRunnerListener<V>
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("create", "true");

    /*
     * We have to do that, since we write to a temporary file
     */
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(Feature.AUTO_CLOSE_TARGET);
    private static final int BUFSIZE = 16384;

    private final Path traceFile;
    private final BufferedWriter traceWriter;
    private final Path zipPath;
    private final JsonGenerator generator;

    private InputBuffer inputBuffer;
    private long startDate;
    private long startNanos;
    private boolean startNanosSet = false;

    public TracingParseRunnerListener(final Path zipPath)
    {
        if (Files.exists(zipPath, LinkOption.NOFOLLOW_LINKS))
            throw new RuntimeException("file " + zipPath + " already exists");

        this.zipPath = zipPath;

        try {
            traceFile = Files.createTempFile("trace", ".json");
            traceWriter = Files.newBufferedWriter(traceFile, UTF_8);
            generator = MAPPER.getFactory().createGenerator(traceWriter);
            generator.writeStartArray();
        } catch (IOException e) {
            throw new RuntimeException("failed to initialize trace", e);
        }
    }

    public TracingParseRunnerListener(final String zipPath)
        throws IOException
    {
        this(Paths.get(zipPath));
    }

    public TracingParseRunnerListener(final File file)
        throws IOException
    {
        this(file.toPath());
    }

    @Override
    public void beforeParse(final PreParseEvent<V> event)
    {
        inputBuffer = event.getContext().getInputBuffer();
        startDate = System.currentTimeMillis();
    }

    @Override
    public void beforeMatch(final PreMatchEvent<V> event)
    {
        final TraceEvent traceEvent = TraceEvent.before(event.getContext());
        final long nanoseconds = System.nanoTime();
        if (!startNanosSet) {
            startNanos = nanoseconds;
            startNanosSet = true;
        }
        traceEvent.setNanoseconds(nanoseconds - startNanos);
        writeEvent(traceEvent);
    }

    @Override
    public void matchSuccess(final MatchSuccessEvent<V> event)
    {
        final long nanos = System.nanoTime();
        final TraceEvent traceEvent = TraceEvent.success(event.getContext());
        traceEvent.setNanoseconds(nanos - startNanos);
        writeEvent(traceEvent);
    }

    @Override
    public void matchFailure(final MatchFailureEvent<V> event)
    {
        final long nanos = System.nanoTime();
        final TraceEvent traceEvent = TraceEvent.failure(event.getContext());
        traceEvent.setNanoseconds(nanos - startNanos);
        writeEvent(traceEvent);
    }

    @Override
    public void afterParse(final PostParseEvent<V> event)
    {
        final ParseRunInfo runInfo = new ParseRunInfo(startDate, inputBuffer);

        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (
            final JsonGenerator gen = generator;
        ) {
            gen.writeEndArray();
            traceWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("failed to write event file", e);
        }

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        ) {
            Files.move(traceFile, zipfs.getPath("/trace.json"));
            copyRunInfo(zipfs, runInfo);
            copyInputText(zipfs);
        } catch (IOException e) {
            throw new RuntimeException("failed to generate zip file", e);
        }
    }

    private void writeEvent(final TraceEvent event)
    {
        try {
            generator.writeObject(event);
        } catch (IOException e) {
            throw new RuntimeException("failed to write event to file", e);
        }
    }

    private void copyRunInfo(final FileSystem zipfs, final ParseRunInfo runInfo)
        throws IOException
    {
        final Path path = zipfs.getPath("/info.json");

        try (
            final BufferedWriter writer = Files.newBufferedWriter(path, UTF_8);
        ) {
            MAPPER.writeValue(writer, runInfo);
            writer.flush();
        }
    }

    private void copyInputText(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath("/input.txt");
        final int length = inputBuffer.length();

        int start = 0;
        String s;

        try (
            final BufferedWriter writer = Files.newBufferedWriter(path, UTF_8);
        ) {
            while (start < length) {
                // Note: relies on the fact that boundaries are adjusted
                s = inputBuffer.extract(start, start + BUFSIZE);
                writer.write(s);
                start += BUFSIZE;
            }

            writer.flush();
        }
    }
}
