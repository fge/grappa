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

import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.IndexRange;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.Position;
import org.parboiled.support.ValueStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface MatcherContext<V>
    extends Context<V>
{
    @Override
    MatcherContext<V> getParent();

    @Nonnull
    @Override
    InputBuffer getInputBuffer();

    @Override
    int getStartIndex();

    @Override
    Matcher getMatcher();

    @Override
    char getCurrentChar();

    @Override
    List<ParseError> getParseErrors();

    @Override
    int getCurrentIndex();

    @Override
    MatcherPath getPath();

    @Override
    int getLevel();

    @Override
    boolean fastStringMatching();

    @Override
    @Nonnull
    List<Node<V>> getSubNodes();

    @Override
    boolean inPredicate();

    @Override
    boolean inErrorRecovery();

    @Override
    boolean isNodeSuppressed();

    @Override
    boolean hasError();

    @Override
    String getMatch();

    @Override
    char getFirstMatchChar();

    @Override
    int getMatchStartIndex();

    @Override
    int getMatchEndIndex();

    @Override
    int getMatchLength();

    @Override
    Position getPosition();

    @Override
    IndexRange getMatchRange();

    @Override
    ValueStack<V> getValueStack();

    void setMatcher(Matcher matcher);

    void setStartIndex(int startIndex);

    void setCurrentIndex(int currentIndex);

    void setInErrorRecovery(boolean flag);

    void advanceIndex(int delta);

    Node<V> getNode();

    int getIntTag();

    void setIntTag(int intTag);

    void markError();

    Boolean hasMismatched();

    void memoizeMismatch();

    void createNode();

    /*
     * TODO! Only called from ActionMatcher :(
     */
    MatcherContext<V> getBasicSubContext();

    MatcherContext<V> getSubContext(Matcher matcher);

    boolean runMatcher();
}
