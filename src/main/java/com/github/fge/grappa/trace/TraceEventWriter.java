package com.github.fge.grappa.trace;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.matchers.base.Matcher;
import org.parboiled.MatcherContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/*
 * Order of fields:
 *
 * - event type (the ordinal!);
 * - index;
 * - level;
 * - matcher;
 * - matcher type;
 * - matcher class;
 * - nanoseconds.
 *
 * No path anymore; useless, it can be built again if needed.
 *
 * The nanoseconds are written after the event only.
 *
 * Writes in CSV format, with semicolon as the separator; semicolons themselves
 * are escaped with a blackslash.
 */
@ParametersAreNonnullByDefault
public final class TraceEventWriter
{
    private static final int BEFORE_MATCH
        = TraceEventType.BEFORE_MATCH.ordinal();
    private static final int MATCH_SUCCESS
        = TraceEventType.MATCH_SUCCESS.ordinal();
    private static final int MATCH_FAILURE
        = TraceEventType.MATCH_FAILURE.ordinal();

    private final Writer writer;
    private final StringBuilder sb = new StringBuilder();

    public TraceEventWriter(final Writer writer)
    {
        this.writer = Objects.requireNonNull(writer);
    }

    public void writeBefore(final MatcherContext<?> context)
    {
        final Matcher matcher = context.getMatcher();
        @SuppressWarnings("ConstantConditions")
        final String name = matcher.getClass().getSimpleName();

        sb.setLength(0);
        sb.append(BEFORE_MATCH).append(';')
            .append(context.getCurrentIndex()).append(';')
            .append(context.getLevel()).append(';')
            .append(matcher.toString().replace(";", "\\;")).append(';')
            .append(name.isEmpty() ? "(anonymous)" : name).append(';');

        try {
            writer.append(sb);
        } catch (IOException e) {
            throw new GrappaException("failed to write trace event", e);
        }
    }

    public void writeAfter(final MatcherContext<?> context,
        final long start, final long end, final boolean success)
    {
        final Matcher matcher = context.getMatcher();
        @SuppressWarnings("ConstantConditions")
        final String name = matcher.getClass().getSimpleName();

        sb.setLength(0);

        sb.append(start).append('\n')
            .append(success ? MATCH_SUCCESS : MATCH_FAILURE).append(';')
            .append(context.getCurrentIndex()).append(';')
            .append(context.getLevel()).append(';')
            .append(matcher.toString().replace(";", "\\;")).append(';')
            .append(name.isEmpty() ? "(anonymous)" : name).append(';')
            .append(end).append('\n');

        try {
            writer.append(sb);
        } catch (IOException e) {
            throw new GrappaException("failed to write trace event", e);
        }
    }
}
