/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.parboiled1.grappa.matchers.join;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.errors.GrammarException;
import org.parboiled.matchers.CustomDefaultLabelMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchervisitors.CanMatchEmptyVisitor;
import org.parboiled.matchervisitors.MatcherVisitor;

/**
 * A joining matcher
 *
 * <p>Such a matcher has two submatchers: a "joined" matcher and a "joining"
 * matcher.</p>
 *
 * <p>This matcher will run cycles through both of these; the first will be a
 * run of the "joined" matcher, subsequent cycles will be ("joining", "joined").
 * </p>
 *
 * <p>Therefore:</p>
 *
 * <ul>
 *     <li>one cycle is {@code joined};</li>
 *     <li>two cycles is {@code joined, joining, joined};</li>
 *     <li>etc etc.</li>
 * </ul>
 *
 * <p>This matcher will correctly reset the index to the last successful
 * match; for instance, if the match sequence is {@code joined, joining, joined,
 * joining} and two cycles are enough, it will reset the index to before the
 * last {@code joining} so that subsequent matchers can proceed from there.</p>
 *
 * <p>It is <strong>forbidden</strong> for the "joining" matcher to match an
 * empty sequence. Unfortunately, due to current limitations, this can only
 * be detected at runtime (but also by the {@link CanMatchEmptyVisitor} when
 * used).</p>
 *
 * <p>This matcher is not built directly; its build is initiated by a {@link
 * JoinMatcherBootstrap}. Example:</p>
 *
 * <pre>
 *     Rule threeDigitsExactly()
 *     {
 *         return join(digit()).using('.').times(3);
 *     }
 * </pre>
 *
 * @see JoinMatcherBootstrap
 */
@Beta
public abstract class JoinMatcher
    extends CustomDefaultLabelMatcher<JoinMatcher>
{
    private static final int JOINED_CHILD_INDEX = 0;
    private static final int JOINING_CHILD_INDEX = 1;

    protected final Matcher joined;
    protected final Matcher joining;

    protected JoinMatcher(final Rule joined, final Rule joining)
    {
        super(new Rule[] { joined, joining }, "join");
        this.joined = getChildren().get(JOINED_CHILD_INDEX);
        this.joining = getChildren().get(JOINING_CHILD_INDEX);
    }

    public final Matcher getJoined()
    {
        return joined;
    }

    public final Matcher getJoining()
    {
        return joining;
    }

    /**
     * Accepts the given matcher visitor.
     *
     * @param visitor the visitor
     * @return the value returned by the given visitor
     */
    @Override
    public final <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor);
        return visitor.visit(this);
    }

    /**
     * Tries a match on the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if the match was successful
     */
    @Override
    public final <V> boolean match(final MatcherContext<V> context)
    {
        /*
         * TODO! Check logic
         *
         * At this point, if we have enough cycles, we can't determined whether
         * our joining rule would match empty... Which is illegal.
         */
        int cycles = 0;
        if (!joined.getSubContext(context).runMatcher()) {
            if (!enoughCycles(cycles))
                return false;
            context.createNode();
            return true;
        }

        cycles++;

        Object snapshot = context.getValueStack().takeSnapshot();
        int beforeCycle = context.getCurrentIndex();

        while (runAgain(cycles) && matchCycle(context, beforeCycle)) {
            beforeCycle = context.getCurrentIndex();
            snapshot = context.getValueStack().takeSnapshot();
            cycles++;
        }

        context.getValueStack().restoreSnapshot(snapshot);
        context.setCurrentIndex(beforeCycle);

        if (!enoughCycles(cycles))
            return false;

        context.createNode();
        return true;
    }

    protected abstract boolean runAgain(final int cycles);

    protected abstract boolean enoughCycles(final int cycles);

    protected final <V> boolean matchCycle(final MatcherContext<V> context,
        final int beforeCycle)
    {
        if (!joining.getSubContext(context).runMatcher())
            return false;
        if (context.getCurrentIndex() == beforeCycle)
            throw new GrammarException("joining rule (%s) of a JoinMatcher" +
                " cannot match an empty character sequence!", joining);
        return joined.getSubContext(context).runMatcher();
    }
}
