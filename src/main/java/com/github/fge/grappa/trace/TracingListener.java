package com.github.fge.grappa.trace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.run.MatchFailureEvent;
import com.github.fge.grappa.run.MatchSuccessEvent;
import com.github.fge.grappa.run.ParseRunnerListener;
import com.github.fge.grappa.run.PostParseEvent;
import com.github.fge.grappa.run.PreMatchEvent;
import com.github.fge.grappa.run.PreParseEvent;
import org.parboiled.MatcherContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public final class TracingListener<V>
    extends ParseRunnerListener<V>
{
    private static final String MATCHER_DESCRIPTOR_CSV_HEAD
        = "id;className;type;name\n";
    private static final String NODE_CSV_HEAD
        = "parent;id;level;success;matcherId;start;end;time";
    private static final int BUFSIZE = 16384;

    private static final Map<String, ?> ENV
        = Collections.singletonMap("create", "true");
    private static final String NODE_PATH = "/nodes.csv";
    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String INPUT_TEXT_PATH = "/input.txt";

    private InputBuffer inputBuffer;
    private long startTime;

    private final Map<Matcher, MatcherDescriptor> matcherDescriptors
        = new IdentityHashMap<>();

    private final Map<Matcher, Integer> matcherIds = new IdentityHashMap<>();
    private int nextMatcherId = 0;

    private final Map<Integer, Integer> nodeIds = new HashMap<>();
    private int nextNodeId = 0;

    private final Map<Integer, Integer> prematchMatcherIds = new HashMap<>();
    private final Map<Integer, Integer> prematchIndices = new HashMap<>();
    private final Map<Integer, Long> prematchTimes = new HashMap<>();

    private final Path zipPath;
    private final Path nodeFile;
    private final BufferedWriter writer;
    private final StringBuilder sb = new StringBuilder();

    public TracingListener(final Path zipPath, final boolean delete)
        throws IOException
    {
        this.zipPath = zipPath;
        if (delete)
            Files.deleteIfExists(zipPath);
        nodeFile = Files.createTempFile("nodes", ".csv");
        writer = Files.newBufferedWriter(nodeFile, UTF_8);
        writer.write(NODE_CSV_HEAD);
    }

    @Override
    public void beforeParse(final PreParseEvent<V> event)
    {
        nodeIds.put(-1, -1);
        inputBuffer = event.getContext().getInputBuffer();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void beforeMatch(final PreMatchEvent<V> event)
    {
        final MatcherContext<V> context = event.getContext();
        final Matcher matcher = context.getMatcher();

        Integer id = matcherIds.get(matcher);
        if (id == null) {
            //noinspection UnnecessaryBoxing
            id = Integer.valueOf(nextMatcherId);
            matcherIds.put(matcher, id);
            matcherDescriptors.put(matcher,
                new MatcherDescriptor(nextMatcherId, matcher));
            nextMatcherId++;
        }

        final int level = context.getLevel();

        nodeIds.put(level, nextNodeId);
        nextNodeId++;

        prematchMatcherIds.put(level, id);
        prematchIndices.put(level, context.getCurrentIndex());
        prematchTimes.put(level, System.nanoTime());
    }

    @SuppressWarnings({ "AutoBoxing", "AutoUnboxing" })
    @Override
    public void matchSuccess(final MatchSuccessEvent<V> event)
    {
        final long endTime = System.nanoTime();
        final MatcherContext<V> context = event.getContext();
        final int level = context.getLevel();

        final Integer parentNodeId = nodeIds.get(level - 1);
        final Integer nodeId = nodeIds.get(level);

        final int startIndex = prematchIndices.get(level);
        final int endIndex = context.getCurrentIndex();

        final Integer matcherId = prematchMatcherIds.get(level);

        final long time = endTime - prematchTimes.get(level);

        // Write:
        // parent;id;level;success;matcherId;start;end;time
        sb.setLength(0);
        sb.append(parentNodeId).append(';')
            .append(nodeId).append(';')
            .append(level).append(";1;")
            .append(matcherId).append(';')
            .append(startIndex).append(';')
            .append(endIndex).append(';')
            .append(time).append('\n');
        try {
            writer.append(sb);
        } catch (IOException e) {
            throw cleanup(e);
        }
    }

    @SuppressWarnings({ "AutoBoxing", "AutoUnboxing" })
    @Override
    public void matchFailure(final MatchFailureEvent<V> event)
    {
        final long endTime = System.nanoTime();
        final MatcherContext<V> context = event.getContext();
        final int level = context.getLevel();

        final Integer parentNodeId = nodeIds.get(level - 1);
        final Integer nodeId = nodeIds.get(level);

        final int startIndex = prematchIndices.get(level);
        final int endIndex = context.getCurrentIndex();

        final Integer matcherId = prematchMatcherIds.get(level);

        final long time = endTime - prematchTimes.get(level);

        // Write:
        // parent;id;level;success;matcherId;start;end;time
        sb.setLength(0);
        sb.append(parentNodeId).append(';')
            .append(nodeId).append(';')
            .append(level).append(";0;")
            .append(matcherId).append(';')
            .append(startIndex).append(';')
            .append(endIndex).append(';')
            .append(time).append('\n');
        try {
            writer.append(sb);
        } catch (IOException e) {
            throw cleanup(e);
        }
    }

    @Override
    public void afterParse(final PostParseEvent<V> event)
    {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw cleanup(e);
        }

        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ENV);
        ) {
            Files.move(nodeFile, zipfs.getPath(NODE_PATH));
            copyInputText(zipfs);
            copyMatcherInfo(zipfs);
        } catch (IOException e) {
            throw cleanup(e);
        }
    }

    private void copyInputText(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(INPUT_TEXT_PATH);
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

    private void copyMatcherInfo(final FileSystem zipfs)
    {
        final Path path = zipfs.getPath(MATCHERS_PATH);

        try (
            final BufferedWriter writer = Files.newBufferedWriter(path, UTF_8);
        ) {
            writer.write(MATCHER_DESCRIPTOR_CSV_HEAD);
            for (final MatcherDescriptor descriptor:
                matcherDescriptors.values()) {
                sb.setLength(0);
                sb.append(descriptor.getId()).append(';')
                    .append(descriptor.getClassName()).append(';')
                    .append(descriptor.getType()).append(';')
                    .append(descriptor.getName()).append('\n');
                writer.append(sb);
            }
            writer.flush();
        } catch (IOException e) {
            throw cleanup(e);
        }
    }

    private GrappaException cleanup(final IOException e)
    {
        final GrappaException ret
            = new GrappaException("failed to write event", e);
        try {
            writer.close();
        } catch (IOException e2) {
            ret.addSuppressed(e2);
        }

        try {
            Files.deleteIfExists(nodeFile);
        } catch (IOException e3) {
            ret.addSuppressed(e3);
        }

        return ret;
    }
}
