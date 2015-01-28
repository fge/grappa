package com.github.fge.grappa.tracingtest;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.trace.parser.TraceEventListListener;
import com.github.fge.grappa.trace.parser.TraceEventParser;
import org.parboiled.Parboiled;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;

public final class TraceReadTest
{
    private static final Path ZIP_PATH = Paths.get("/home/fge/t.zip");
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private static final String TRACE_PATH = "/trace.csv";

    public static void main(final String... args)
        throws IOException
    {
        final URI uri = URI.create("jar:" + ZIP_PATH.toUri());

        final TraceEventParser parser
            = Parboiled.createParser(TraceEventParser.class, 20);
        final TraceEventListListener listener = new TraceEventListListener();
        parser.register(listener);
        final EventBasedParseRunner<Void> runner
            = new EventBasedParseRunner<>(parser.traceEvents());

        final Path tmpfile = Paths.get("/tmp/trace.csv");

        try (
            final FileSystem fs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        ) {
            Files.copy(fs.getPath(TRACE_PATH), tmpfile,
                StandardCopyOption.REPLACE_EXISTING);
        }

        final String s = com.google.common.io.Files.toString(tmpfile.toFile(),
            StandardCharsets.UTF_8);
        runner.run(new CharSequenceInputBuffer(s));

        System.out.printf("done");
    }
}
