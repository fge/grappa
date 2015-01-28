package com.github.fge.grappa.trace;

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
 * - matcher type (the ordinal!);
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

    public void writeBefore(final MatcherContext<?> context, final long nanos)
        throws IOException
    {
        final Matcher matcher = context.getMatcher();
        @SuppressWarnings("ConstantConditions")
        final String name = matcher.getClass().getSimpleName();

        sb.setLength(0);
        sb.append(BEFORE_MATCH).append(';')
            .append(context.getCurrentIndex()).append(';')
            .append(context.getLevel()).append(';')
            .append(matcher.toString().replace(";", "\\;")).append(';')
            .append(matcher.getType().ordinal()).append(';')
            .append(name.isEmpty() ? "(anonymous)" : name).append(';')
            .append(nanos).append('\n');

        writer.append(sb);
    }

    public void writeSuccess(final MatcherContext<?> context, final long nanos)
        throws IOException
    {
        final Matcher matcher = context.getMatcher();
        @SuppressWarnings("ConstantConditions")
        final String name = matcher.getClass().getSimpleName();

        sb.setLength(0);

        sb.append(MATCH_SUCCESS).append(';')
            .append(context.getCurrentIndex()).append(';')
            .append(context.getLevel()).append(';')
            .append(matcher.toString().replace(";", "\\;")).append(';')
            .append(matcher.getType().ordinal()).append(';')
            .append(name.isEmpty() ? "(anonymous)" : name).append(';')
            .append(nanos).append('\n');

        writer.append(sb);
    }

    public void writeFailure(final MatcherContext<?> context, final long nanos)
        throws IOException
    {
        final Matcher matcher = context.getMatcher();
        @SuppressWarnings("ConstantConditions")
        final String name = matcher.getClass().getSimpleName();

        sb.setLength(0);

        sb.append(MATCH_FAILURE).append(';')
            .append(context.getCurrentIndex()).append(';')
            .append(context.getLevel()).append(';')
            .append(matcher.toString().replace(";", "\\;")).append(';')
            .append(matcher.getType().ordinal()).append(';')
            .append(name.isEmpty() ? "(anonymous)" : name).append(';')
            .append(nanos).append('\n');

        writer.append(sb);
    }
}
