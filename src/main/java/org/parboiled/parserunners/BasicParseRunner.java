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

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.google.common.base.Preconditions;
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.support.ParsingResult;

/**
 * The most basic of all {@link ParseRunner} implementations. It runs a rule
 * against a given input text and builds a corresponding {@link ParsingResult}
 * instance. However, it does not report any parse errors nor recover from them.
 * Instead it simply marks the ParsingResult as "unmatched" if the input is not
 * valid with regard to the rule grammar.It never causes the parser to perform
 * more than one parsing run and is the fastest way to determine whether a given
 * input conforms to the rule grammar.
 */
public final class BasicParseRunner<V>
    extends AbstractParseRunner<V>
    implements MatchHandler
{
    /**
     * Creates a new BasicParseRunner instance for the given rule.
     *
     * @param rule the parser rule
     */
    public BasicParseRunner(final Rule rule)
    {
        super(rule);
    }

    @Override
    public ParsingResult<V> run(final InputBuffer inputBuffer)
    {
        Preconditions.checkNotNull(inputBuffer, "inputBuffer");
        resetValueStack();

        final MatcherContext<V> rootContext
            = createRootContext(inputBuffer, this);
        final boolean matched = rootContext.runMatcher();
        return createParsingResult(matched, rootContext);
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return context.getMatcher().match(context);
    }
}
