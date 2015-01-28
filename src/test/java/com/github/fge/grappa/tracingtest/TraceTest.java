package com.github.fge.grappa.tracingtest;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.trace.TracingParseRunnerListener;
import com.google.common.io.Files;
import org.parboiled.Parboiled;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TraceTest
{
    private static final Path JSON_SCHEMA = Paths.get("/home/fge/src/perso/"
        + "json-schema-validator/src/main/resources/draftv4/schema");

    public static void main(final String... args)
        throws IOException
    {
        final TracingParseRunnerListener<Void> listener
            = new TracingParseRunnerListener<>(Paths.get("/home/fge/t.zip"),
                true);
        final JsonParser parser = Parboiled.createParser(JsonParser.class);
        final EventBasedParseRunner<Void> runner
            = new EventBasedParseRunner<>(parser.jsonText());
        runner.registerListener(listener);

        final String s = Files.toString(JSON_SCHEMA.toFile(),
            StandardCharsets.UTF_8);
        final InputBuffer buffer = new CharSequenceInputBuffer(s);
        runner.run(buffer);
        System.out.println("done");
    }
}
