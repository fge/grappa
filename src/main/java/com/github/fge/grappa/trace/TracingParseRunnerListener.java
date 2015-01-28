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

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.run.MatchFailureEvent;
import com.github.fge.grappa.run.MatchSuccessEvent;
import com.github.fge.grappa.run.ParseRunnerListener;
import com.github.fge.grappa.run.PostParseEvent;
import com.github.fge.grappa.run.PreMatchEvent;
import com.github.fge.grappa.run.PreParseEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedWriter;
import java.io.Closeable;
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
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public final class TracingParseRunnerListener<V>
    extends ParseRunnerListener<V>
{
    private static final Path DEFAULT_DIRECTORY;

    static {
        final String tmpdir = System.getProperty("java.io.tmpdir", "");
        DEFAULT_DIRECTORY = Paths.get(tmpdir).toAbsolutePath();

        if (!Files.isDirectory(DEFAULT_DIRECTORY))
            throw new ExceptionInInitializerError(tmpdir
                + " is not a directory");

        if (!Files.isWritable(DEFAULT_DIRECTORY))
            throw new ExceptionInInitializerError("no write access to "
                + tmpdir);
    }

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
    private final TraceEventWriter eventWriter;

    private InputBuffer inputBuffer;
    private long startDate;

    public TracingParseRunnerListener(final Path dir, final Path zipPath,
        final boolean deleteIfExists)
    {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(zipPath);

        if (deleteIfExists)
            try {
                Files.deleteIfExists(zipPath);
            } catch (IOException e) {
                throw new GrappaException("cannot delete existing zip", e);
            }
        else if (Files.exists(zipPath, LinkOption.NOFOLLOW_LINKS))
            throw new GrappaException("file " + zipPath + " already exists");

        this.zipPath = zipPath;

        try {
            traceFile = Files.createTempFile(dir, "trace", ".json");
            traceWriter = Files.newBufferedWriter(traceFile, UTF_8);
        } catch (IOException e) {
            throw cleanup("failed to initialize trace", e);
        }

        eventWriter = new TraceEventWriter(traceWriter);
    }

    public TracingParseRunnerListener(final Path zipPath,
        final boolean deleteIfExists)
    {
        this(DEFAULT_DIRECTORY, zipPath, deleteIfExists);
    }

    public TracingParseRunnerListener(final Path zipPath)
    {
        this(DEFAULT_DIRECTORY, zipPath, false);
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
        try {
            eventWriter.writeBefore(event.getContext(), System.nanoTime());
        } catch (IOException e) {
            throw cleanup("failed to write event", e);
        }
    }

    @Override
    public void matchSuccess(final MatchSuccessEvent<V> event)
    {
        try {
            eventWriter.writeSuccess(event.getContext(), System.nanoTime());
        } catch (IOException e) {
            throw cleanup("failed to write event", e);
        }
    }

    @Override
    public void matchFailure(final MatchFailureEvent<V> event)
    {
        try {
            eventWriter.writeFailure(event.getContext(), System.nanoTime());
        } catch (IOException e) {
            throw cleanup("failed to write event", e);
        }
    }

    @Override
    public void afterParse(final PostParseEvent<V> event)
    {
        //noinspection UnusedDeclaration
        try (
            final Closeable closeable = traceWriter;
        ) {
            traceWriter.flush();
        } catch (IOException e) {
            throw cleanup("failed to close trace file", e);
        }

        final ParseRunInfo runInfo = new ParseRunInfo(startDate, inputBuffer);
        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        ) {
            Files.move(traceFile, zipfs.getPath("/trace.csv"));
            copyRunInfo(zipfs, runInfo);
            copyInputText(zipfs);
        } catch (IOException e) {
            throw cleanup("failed to generate zip file", e);
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

    private GrappaException cleanup(final String msg, final Throwable throwable)
    {
        try {
            Files.deleteIfExists(traceFile);
        } catch (IOException e) {
            throwable.addSuppressed(e);
        }

        return new GrappaException(msg, throwable);
    }
}
