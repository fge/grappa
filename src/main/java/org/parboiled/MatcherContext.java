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

package org.parboiled;

import org.parboiled.matchers.Matcher;

public interface MatcherContext<V>
    extends Context<V>
{
    MatcherContext<V> getParent();

    /*
     * TODO! Only used in ActionMatcher, MemoMismatchesMatcher,
     * VarFramingMatcher
     */
    void setMatcher(Matcher matcher);

    /*
     * TODO: only used from RecoveringParseRunner.Handler
     */
    void setStartIndex(int startIndex);

    void setCurrentIndex(int currentIndex);

    /*
     * TODO: only used from RecoveringParseRunner.Handler
     */
    void setInErrorRecovery(boolean flag);

    void advanceIndex(int delta);

    Node<V> getNode();

    int getIntTag();

    void setIntTag(int intTag);

    /*
     * TODO: only used from RecoveringParseRunner.Handler
     */
    void markError();

    /*
     * TODO: only used from MemoMismatchesMatcher
     */
    boolean hasMismatched();

    /*
     * TODO: only used from MemoMismatchesMatcher
     */
    void memoizeMismatch();

    void createNode();

    /*
     * TODO! Only called from ActionMatcher :(
     */
    MatcherContext<V> getBasicSubContext();

    MatcherContext<V> getSubContext(Matcher matcher);

    boolean runMatcher();
}
