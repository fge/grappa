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

package com.github.fge.grappa.run;

import com.github.fge.grappa.matchers.base.Matcher;
import com.github.fge.grappa.run.context.MatcherContext;

/**
 * Interface used to run a {@link Matcher} against an input text
 *
 * <p>The {@link MatcherContext} as an argument holds the matcher to be
 * actually run by invoking {@link MatcherContext#getMatcher()}.</p>
 */
public interface MatchHandler
{
    /**
     * Runs the given MatcherContext.
     *
     * @param context the MatcherContext
     * @return true if matched
     */
    <V> boolean match(MatcherContext<V> context);
}
