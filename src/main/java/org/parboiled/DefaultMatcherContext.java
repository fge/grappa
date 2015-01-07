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

package org.parboiled;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.matchers.ActionMatcher;
import com.github.parboiled1.grappa.matchers.base.Matcher;
import com.github.parboiled1.grappa.matchers.delegate.SequenceMatcher;
import com.github.parboiled1.grappa.matchers.predicates.TestMatcher;
import com.github.parboiled1.grappa.matchers.predicates.TestNotMatcher;
import com.github.parboiled1.grappa.matchers.wrap.ProxyMatcher;
import com.github.parboiled1.grappa.stack.ValueStack;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import org.parboiled.errors.BasicParseError;
import org.parboiled.errors.GrammarException;
import org.parboiled.errors.ParseError;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.CharsEscaper;
import org.parboiled.support.Checks;
import org.parboiled.support.IndexRange;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.MatcherPosition;
import org.parboiled.trees.ParseTreeUtils;
import org.parboiled.support.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.parboiled1.grappa.matchers.MatcherUtils.unwrap;
import static org.parboiled.errors.ErrorUtils.printParseError;

/**
 * <p>The Context implementation orchestrating most of the matching process.</p>
 *
 * <p>The parsing process works as following:</p>
 *
 * <p>After the rule tree (which is in fact a directed and potentially even
 * cyclic graph of {@link Matcher} instances) has been created a root
 * MatcherContext is instantiated for the root rule (Matcher). A subsequent call
 * to {@link #runMatcher()} starts the parsing process.</p>
 *
 * <p>The MatcherContext delegates to a given {@link MatchHandler} to call
 * {@link Matcher#match(MatcherContext)}, passing itself to the Matcher which
 * executes its logic, potentially calling sub matchers. For each sub matcher
 * the matcher creates/initializes a subcontext with {@link
 * Matcher#getSubContext(MatcherContext)} and then calls {@link #runMatcher()}
 * on it.</p>
 *
 * <p>This basically creates a stack of MatcherContexts, each corresponding to
 * their rule matchers. The MatcherContext instances serve as companion objects
 * to the matchers, providing them with support for building the parse tree
 * nodes, keeping track of input locations and error recovery.</p>
 *
 * <p>At each point during the parsing process the matchers and action
 * expressions have access to the current MatcherContext and all "open" parent
 * MatcherContexts through the {@link #getParent()} chain.</p>
 *
 * <p>For performance reasons subcontext instances are reused instead of being
 * recreated. If a MatcherContext instance returns null on a {@link
 * #getMatcher()} call it has been retired (is invalid) and is waiting to be
 * reinitialized with a new Matcher by its parent</p>
 */
public final class DefaultMatcherContext<V>
    implements MatcherContext<V>
{
    private final InputBuffer inputBuffer;
    private final ValueStack<V> valueStack;
    private final List<ParseError> parseErrors;
    private final MatchHandler matchHandler;
    private final DefaultMatcherContext<V> parent;
    private final int level;
    private final boolean fastStringMatching;
    private final Set<MatcherPosition> memoizedMismatches;

    private DefaultMatcherContext<V> subContext;
    private int startIndex;
    private int currentIndex;
    private char currentChar;
    private Matcher matcher;
    private Node<V> node;
    // TODO! Replace!
    private List<Node<V>> subNodes = Lists.newArrayList();
    private MatcherPath path;
    private int intTag;
    private boolean hasError;
    private boolean nodeSuppressed;
    private boolean inErrorRecovery;

    /**
     * Initializes a new root MatcherContext.
     *
     * @param inputBuffer the InputBuffer for the parsing run
     * @param valueStack the ValueStack instance to use for the parsing run
     * @param parseErrors the parse error list to create ParseError objects in
     * @param matchHandler the MatcherHandler to use for the parsing run
     * @param matcher the root matcher
     * @param fastStringMatching <p>Fast string matching "short-circuits" the
     * default practice of treating string rules as simple Sequence of character
     * rules. When fast string matching is enabled strings are matched at once,
     * without relying on inner CharacterMatchers. Even though this can lead to
     * significant increases of parsing performance it does not play well with
     * error reporting and recovery, which relies on character level matches.
     * Therefore a {@link ReportingParseRunner} only enables fast string
     * matching during the first parsing run and disables it once the input has
     * proven to contain errors.</p>
     */
    public DefaultMatcherContext(@Nonnull final InputBuffer inputBuffer,
        @Nonnull final ValueStack<V> valueStack,
        @Nonnull final List<ParseError> parseErrors,
        @Nonnull final MatchHandler matchHandler,
        @Nonnull final Matcher matcher, final boolean fastStringMatching)
    {

        this(Preconditions.checkNotNull(inputBuffer, "inputBuffer"),
            Preconditions.checkNotNull(valueStack, "valueStack"),
            Preconditions.checkNotNull(parseErrors, "parseErrors"),
            Preconditions.checkNotNull(matchHandler, "matchHandler"), null, 0,
            fastStringMatching, new HashSet<MatcherPosition>());
        currentChar = inputBuffer.charAt(0);
        Preconditions.checkNotNull(matcher);
        // TODO: what the...
        this.matcher = ProxyMatcher.unwrap(matcher);
        nodeSuppressed = matcher.isNodeSuppressed();
    }

    private DefaultMatcherContext(final InputBuffer inputBuffer,
        final ValueStack<V> valueStack, final List<ParseError> parseErrors,
        final MatchHandler matchHandler,
        @Nullable final DefaultMatcherContext<V> parent,
        final int level, final boolean fastStringMatching,
        final Set<MatcherPosition> memoizedMismatches)
    {
        this.inputBuffer = inputBuffer;
        this.valueStack = valueStack;
        this.parseErrors = parseErrors;
        this.matchHandler = matchHandler;
        this.parent = parent;
        this.level = level;
        this.fastStringMatching = fastStringMatching;
        this.memoizedMismatches = memoizedMismatches;
    }

    @Override
    public String toString()
    {
        return getPath().toString();
    }

    //////////////////////////////// CONTEXT INTERFACE ////////////////////////////////////

    @Override
    public MatcherContext<V> getParent()
    {
        return parent;
    }

    @Nonnull
    @Override
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }

    @Override
    public int getStartIndex()
    {
        return startIndex;
    }

    @Override
    public Matcher getMatcher()
    {
        return matcher;
    }

    @Override
    public char getCurrentChar()
    {
        return currentChar;
    }

    @Nonnull
    @Override
    public List<ParseError> getParseErrors()
    {
        return parseErrors;
    }

    @Override
    public int getCurrentIndex()
    {
        return currentIndex;
    }

    @Nonnull
    @Override
    public MatcherPath getPath()
    {
        if (path != null)
            return path;

        path = new MatcherPath(new MatcherPath.Element(matcher, startIndex,
            level), parent != null ? parent.getPath() : null);
        return path;
    }

    @Override
    public int getLevel()
    {
        return level;
    }

    @Override
    public boolean fastStringMatching()
    {
        return fastStringMatching;
    }

    @Override
    @Nonnull
    public List<Node<V>> getSubNodes()
    {
        if (matcher.isNodeSkipped())
            return subNodes;

        final Deque<Node<V>> remaining = Queues.newArrayDeque(subNodes);

        final List<Node<V>> ret = Lists.newArrayList();
        collectSubNodes(remaining, ret);
        Collections.reverse(ret);
        return ret;
    }

    private static <E> void collectSubNodes(final Deque<Node<E>> remaining,
        final List<Node<E>> into)
    {
        Node<E> head;
        while (!remaining.isEmpty()) {
            head = remaining.pop();
            if (!head.getMatcher().isNodeSkipped()) {
                into.add(head);
                continue;
            }
            collectSubNodes(Queues.newArrayDeque(head.getChildren()), into);
        }
    }

    @Override
    public boolean inPredicate()
    {
        if (matcher instanceof TestMatcher)
            return true;

        if (matcher instanceof TestNotMatcher)
            return true;

        if (parent == null)
            return false;

        return parent.inPredicate();
    }

    @Override
    public boolean inErrorRecovery()
    {
        return inErrorRecovery;
    }

    @Override
    public boolean isNodeSuppressed()
    {
        return nodeSuppressed;
    }

    @Override
    public boolean hasError()
    {
        return hasError;
    }

    @Override
    public String getMatch()
    {
        checkActionContext();
        final DefaultMatcherContext<V> prevContext = subContext;
        if (!hasError)
            return inputBuffer.extract(prevContext.startIndex,
                prevContext.currentIndex);

        final Node<V> prevNode = prevContext.node;
        return prevNode != null
            ? ParseTreeUtils.getNodeText(prevNode, inputBuffer)
            : "";
    }

    @Override
    public char getFirstMatchChar()
    {
        checkActionContext();
        final int index = subContext.startIndex;
        if (subContext.currentIndex > index)
            return inputBuffer.charAt(index);

        // TODO: figure out why it says that
        throw new GrammarException("getFirstMatchChar called but previous rule" +
            " did not match anything");
    }

    @Override
    public int getMatchStartIndex()
    {
        checkActionContext();
        return subContext.startIndex;
    }

    @Override
    public int getMatchEndIndex()
    {
        checkActionContext();
        return subContext.currentIndex;
    }

    @Override
    public int getMatchLength()
    {
        checkActionContext();
        return subContext.currentIndex - subContext.getStartIndex();
    }

    @Override
    public Position getPosition()
    {
        return inputBuffer.getPosition(currentIndex);
    }

    @Override
    public IndexRange getMatchRange()
    {
        checkActionContext();
        return new IndexRange(subContext.startIndex, subContext.currentIndex);
    }

    // TODO: pain point!
    private void checkActionContext()
    {
        // make sure all the constraints are met
        final boolean condition = unwrap(matcher) instanceof SequenceMatcher
            && intTag > 0
            && subContext.matcher instanceof ActionMatcher;
        Checks.ensure(condition,
            "Illegal call to getMatch(), getMatchStartIndex()," +
            " getMatchEndIndex() or getMatchRange(), only valid in Sequence" +
            " rule actions that are not in first position"
        );
    }

    @Override
    public ValueStack<V> getValueStack()
    {
        return valueStack;
    }

    //////////////////////////////// PUBLIC ////////////////////////////////////

    @Override
    public void setMatcher(final Matcher matcher)
    {
        this.matcher = matcher;
    }

    @Override
    public void setStartIndex(final int startIndex)
    {
        Preconditions.checkArgument(startIndex >= 0);
        this.startIndex = startIndex;
    }

    @Override
    public void setCurrentIndex(final int currentIndex)
    {
        Preconditions.checkArgument(currentIndex >= 0);
        this.currentIndex = currentIndex;
        currentChar = inputBuffer.charAt(currentIndex);
    }

    @Override
    public void advanceIndex(final int delta)
    {
        currentIndex += delta;
        currentChar = inputBuffer.charAt(currentIndex);
    }

    @Override
    public Node<V> getNode()
    {
        return node;
    }

    @Override
    public int getIntTag()
    {
        return intTag;
    }

    @Override
    public void setIntTag(final int intTag)
    {
        this.intTag = intTag;
    }

    @Override
    public boolean hasMismatched()
    {
        return memoizedMismatches.contains(MatcherPosition.at(matcher,
            currentIndex));
    }

    @Override
    public void memoizeMismatch()
    {
        memoizedMismatches.add(MatcherPosition.at(matcher, currentIndex));
    }

    @Override
    public void createNode()
    {
        if (nodeSuppressed)
            return;

        node = new DefaultParsingNode<>(matcher, getSubNodes(), startIndex,
            currentIndex, valueStack.isEmpty() ? null : valueStack.peek(),
            hasError);
        if (parent != null) {
            parent.subNodes.add(0, node);
        }
    }

    @Override
    public final MatcherContext<V> getBasicSubContext()
    {
        if (subContext == null) {
            // init new level
            subContext = new DefaultMatcherContext<>(inputBuffer, valueStack,
                parseErrors, matchHandler, this, level + 1, fastStringMatching,
                memoizedMismatches);
        } else {
            // we always need to reset the MatcherPath, even for actions
            subContext.path = null;
        }
        return subContext;
    }

    @Override
    public final MatcherContext<V> getSubContext(final Matcher matcher)
    {
        final DefaultMatcherContext<V> sc
            = (DefaultMatcherContext<V>) getBasicSubContext();
        sc.setMatcher(matcher);
        sc.setStartIndex(currentIndex);
        sc.setCurrentIndex(currentIndex);
        sc.currentChar = currentChar;
        //sc.node = null;
        sc.subNodes = Lists.newArrayList();
        sc.nodeSuppressed = nodeSuppressed
            || this.matcher.areSubnodesSuppressed()
            || matcher.isNodeSuppressed();
        sc.hasError = false;
        return sc;
    }

    @Override
    public boolean runMatcher()
    {
        try {
            final boolean ret = matchHandler.match(this);
            // Retire this context
            // TODO: what does the above really mean?
            matcher = null;
            if (ret && parent != null) {
                parent.currentIndex = currentIndex;
                parent.currentChar = currentChar;
            }
            return ret;
        } catch (ParserRuntimeException e) {
            throw e; // don't wrap, just bubble up
        } catch (Throwable e) { // TODO: Throwable? What the...
            final String msg = String.format(
                "Error while parsing %s '%s' at input position\n%s",
                matcher instanceof ActionMatcher ? "action" : "rule", getPath(),
                e);
            final BasicParseError error = new BasicParseError(inputBuffer,
                currentIndex, CharsEscaper.INSTANCE.escape(msg));
            // TODO: UGLY
            throw new ParserRuntimeException(e, printParseError(error));
        }
    }
}
