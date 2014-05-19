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

package org.parboiled.support;

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.github.parboiled1.grappa.annotations.WillBePrivate;
import com.google.common.base.Preconditions;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.BasicParseRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A simple container encapsulating the result of a parsing run.
 */
@WillBeFinal(version = "1.1")
public class ParsingResult<V>
{

    /**
     * DO NOT USE DIRECTLY!
     *
     * Use {@link #hasMatch()} instead
     */
    @WillBePrivate(version = "1.1")
    public final boolean matched;

    /**
     * The root node of the parse tree created by the parsing run. This field will only be non-null when
     * parse-tree-building has been enabled.
     */
    @WillBePrivate(version = "1.1")
    public final Node<V> parseTreeRoot;

    /**
     * The top value of the value stack at the end of the parsing run or null, if the value stack is empty.
     */
    @WillBePrivate(version = "1.1")
    @Nullable // TODO: replace with Optional?
    public final V resultValue;

    /**
     * The ValueStack used during the parsing run containing all values not popped of the stack by the parser.
     */
    @WillBePrivate(version = "1.1")
    public final ValueStack<V> valueStack;

    /**
     * The list of parse errors created during the parsing run.
     */
    @WillBePrivate(version = "1.1")
    public final List<ParseError> parseErrors;

    /**
     * The underlying input buffer.
     */
    @WillBePrivate(version = "1.1")
    public final InputBuffer inputBuffer;

    /**
     * Creates a new ParsingResult.
     *
     * @param matched true if the rule matched the input
     * @param parseTreeRoot the parse tree root node
     * @param valueStack the value stack of the parsing run
     * @param parseErrors the list of parse errors
     * @param inputBuffer the input buffer
     */
    public ParsingResult(final boolean matched, final Node<V> parseTreeRoot,
        @Nonnull final ValueStack<V> valueStack,
        @Nonnull final List<ParseError> parseErrors,
        @Nonnull final InputBuffer inputBuffer)
    {
        this.matched = matched;
        this.parseTreeRoot = parseTreeRoot;
        this.valueStack = Preconditions.checkNotNull(valueStack);
        resultValue = valueStack.isEmpty() ? null : valueStack.peek();
        this.parseErrors = Preconditions.checkNotNull(parseErrors);
        this.inputBuffer = Preconditions.checkNotNull(inputBuffer);
    }


    /**
     * Return true if this parse result is a match
     *
     * <p>Note that a result NOT having a match does not mean that it has
     * collected parse errors (see {@link #hasCollectedParseErrors()}; a
     * {@link BasicParseRunner}, for instance, does not collect errors.</p>
     *
     * @return see description
     */
    public final boolean hasMatch()
    {
        return matched;
    }

    /**
     * Has this result collected any parse errors?
     *
     * <p><strong>note</strong>: this method does not guarantee that the result
     * is a success; for this use {@link #hasMatch()} instead.</p>
     *
     * @return true if the parse error list is not empty
     */
    // TODO: not clear whether parseErrors can be null
    public final boolean hasCollectedParseErrors()
    {
        return parseErrors != null && !parseErrors.isEmpty();
    }

    /**
     * @return true if this parsing result contains parsing errors.
     *
     * @deprecated name is confusing; use {@link #hasMatch()} to see if this
     * result is a match and {@link #hasCollectedParseErrors()} if this result
     * has collected parse errors.
     */
    @Deprecated
    @Unused
    public boolean hasErrors()
    {
        return !matched;
    }
}
