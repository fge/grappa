/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package org.parboiled.parserunners;

import com.github.parboiled1.grappa.cleanup.DoNotUse;
import com.github.parboiled1.grappa.cleanup.Unused;
import com.github.parboiled1.grappa.cleanup.WillBeRemoved;
import com.github.parboiled1.grappa.misc.SinkAdapter;
import com.github.parboiled1.grappa.misc.SystemOutCharSource;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.io.CharSink;
import org.parboiled.Context;
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.Sink;
import org.parboiled.common.Tuple2;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * A {@link ParseRunner} implementation used for debugging purposes.
 * It exhibits the same behavior as the {@link ReportingParseRunner} but collects debugging information as to which
 * rules did match and which didn't.
 */
// TODO: get rid of nulls
public class TracingParseRunner<V>
    extends ReportingParseRunner<V>
    implements MatchHandler
{
    private Predicate<Tuple2<Context<?>, Boolean>> filter
        = Predicates.alwaysTrue();
    private CharSink log = SystemOutCharSource.INSTANCE;
    private MatcherPath lastPath;

    /**
     * Creates a new TracingParseRunner instance without filter and a console log for the given rule.
     *
     * @param rule the parser rule
     */
    public TracingParseRunner(final Rule rule)
    {
        super(rule);
    }

    /**
     * Attaches the given filter to this TracingParseRunner instance.
     * The given filter is used to select the matchers to print tracing
     * statements for.
     *
     * @param filter the matcher filter selecting the matchers to print tracing
     * statements for
     * @return this instance
     */
    public TracingParseRunner<V> withFilter(
        @Nonnull final Predicate<Tuple2<Context<?>, Boolean>> filter)
    {
        this.filter = Preconditions.checkNotNull(filter, "filter");
        return this;
    }

    @Deprecated
    @Unused
    @DoNotUse
    @WillBeRemoved(version = "1.1")
    public Predicate<Tuple2<Context<?>, Boolean>> getFilter()
    {
        return filter;
    }

    /**
     * Attaches the given log to this TracingParseRunner instance.
     *
     * @param log the log to use
     * @return this instance
     */
    @Deprecated
    @DoNotUse
    @Unused
    @WillBeRemoved(version = "1.1")
    public TracingParseRunner<V> withLog(@Nonnull final Sink<String> log)
    {
        Preconditions.checkNotNull(log);
        this.log = new SinkAdapter(log);
        return this;
    }

    @Deprecated
    @DoNotUse
    @Unused
    @WillBeRemoved(version = "1.1")
    public Sink<String> getLog()
    {
        @SuppressWarnings("unchecked")
        final Sink<String> sink = (Sink<String>) log;
        return sink;
    }

    @Override
    protected ParsingResult<V> runBasicMatch(final InputBuffer inputBuffer)
    {
        try {
            log.write("Starting new parsing run\n");
        } catch (IOException e) {
            throw new RuntimeException("cannot write to CharSink", e);
        }
        lastPath = null;

        final MatcherContext<V> rootContext
            = createRootContext(inputBuffer, this, true);
        final boolean matched = rootContext.runMatcher();
        return createParsingResult(matched, rootContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean match(final MatcherContext<?> context)
    {
        final Matcher matcher = context.getMatcher();
        final boolean matched = matcher.match(context);
        final Tuple2<Context<?>, Boolean> input
            = new Tuple2<Context<?>, Boolean>(context, matched);

        if (filter.apply(input))
            try {
                print(context, matched); // set line-dependent breakpoint here
            } catch (IOException e) {
                throw new RuntimeException("cannot write to CharSink", e);
            }


        return matched;
    }

    private void print(final MatcherContext<?> context, final boolean matched)
        throws IOException
    {
        final int currentIndex = context.getCurrentIndex();
        final Position pos
            = context.getInputBuffer().getPosition(currentIndex);
        final MatcherPath path = context.getPath();
        final MatcherPath prefix = lastPath != null
            ? path.commonPrefix(lastPath)
            : null;

        if (prefix != null && prefix.length() > 1)
            log.write("..(" + (prefix.length() - 1) + ")../");

        log.write(path.toString(prefix != null ? prefix.parent : null));

        final String line = context.getInputBuffer().extractLine(pos.line);
        log.write(", " + (matched ? "matched" : "failed") + ", cursor at "
            + pos.line + ':' + pos.column + " after \""
            + line.substring(0, Math.min(line.length(), pos.column - 1))
            + "\"\n"
        );
        lastPath = path;
    }
}

