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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.parboiled.Context;
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.ConsoleSink;
import org.parboiled.common.Sink;
import org.parboiled.common.Tuple2;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;

import javax.annotation.Nonnull;

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
    private Predicate<Tuple2<Context<?>, Boolean>> filter;
    private Sink<String> log;
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
     * The given filter is used to select the matchers to print tracing statements for.
     *
     * @param filter the matcher filter selecting the matchers to print tracing statements for. Must be of type
     * Predicate&lt;Tuple2&lt;Context&lt;?&gt;, Boolean&gt;&gt;.
     * @return this instance
     */
    public TracingParseRunner<V> withFilter(
        @Nonnull final Predicate<Tuple2<Context<?>, Boolean>> filter)
    {
        this.filter = Preconditions.checkNotNull(filter, "filter");
        return this;
    }

    public Predicate<Tuple2<Context<?>, Boolean>> getFilter()
    {
        if (filter == null) {
            withFilter(Predicates.<Tuple2<Context<?>, Boolean>>alwaysTrue());
        }
        return filter;
    }

    /**
     * Attaches the given log to this TracingParseRunner instance.
     *
     * @param log the log to use
     * @return this instance
     */
    public TracingParseRunner<V> withLog(final Sink<String> log)
    {
        this.log = log;
        return this;
    }

    public Sink<String> getLog()
    {
        if (log == null) {
            withLog(new ConsoleSink());
        }
        return log;
    }

    @Override
    protected ParsingResult<V> runBasicMatch(final InputBuffer inputBuffer)
    {
        getLog().receive("Starting new parsing run\n");
        lastPath = null;

        final MatcherContext<V> rootContext = createRootContext(inputBuffer,
            this, true);
        final boolean matched = rootContext.runMatcher();
        return createParsingResult(matched, rootContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean match(final MatcherContext<?> context)
    {
        final Matcher matcher = context.getMatcher();
        final boolean matched = matcher.match(context);
        if (getFilter()
            .apply(new Tuple2<Context<?>, Boolean>(context, matched))) {
            print(context, matched); // set line-dependent breakpoint here
        }
        return matched;
    }

    private void print(final MatcherContext<?> context, final boolean matched)
    {
        final Position pos = context.getInputBuffer()
            .getPosition(context.getCurrentIndex());
        final MatcherPath path = context.getPath();
        final MatcherPath prefix = lastPath != null ? path
            .commonPrefix(lastPath) : null;
        if (prefix != null && prefix.length() > 1)
            getLog().receive("..(" + (prefix.length() - 1) + ")../");
        getLog().receive(path.toString(prefix != null ? prefix.parent : null));
        final String line = context.getInputBuffer().extractLine(pos.line);
        getLog().receive(
            ", " + (matched ? "matched" : "failed") + ", cursor at " + pos.line
                + ':' + pos.column +
                " after \"" + line
                .substring(0, Math.min(line.length(), pos.column - 1))
                + "\"\n");
        lastPath = path;
    }
}

