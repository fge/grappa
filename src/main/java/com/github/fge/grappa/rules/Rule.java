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

package com.github.fge.grappa.rules;

import com.github.fge.grappa.matchers.join.JoinMatcher;
import org.parboiled.Context;
import org.parboiled.annotations.SkipNode;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

/**
 * Describes the return values of parser rule production methods.
 */
public interface Rule
{
    /**
     * Attaches a label to this Rule.
     * Corresponds to the @Label annotation.
     *
     * @param label the label
     * @return this Rule
     */
    Rule label(String label);

    /**
     * Instructs parboiled to not create a parse tree node for this rule
     * <b>and all subrules</b>, which can significantly increase parsing
     * performance. Corresponds to the {@link SuppressNode} annotation.
     *
     * @return this Rule
     */
    Rule suppressNode();

    /**
     * Instructs parboiled to not create parse tree nodes for the subrules of
     * this rule, which can significantly increase parsing performance.
     * Corresponds to the {@link SuppressSubnodes} annotation.
     *
     * @return this Rule
     */
    Rule suppressSubnodes();

    /**
     * Instructs parboiled to not create a parse tree node for this rule. The
     * parse tree nodes of all subrules are directly attached to the parent of
     * this rule (or more correctly: the first ancestor not having been marked
     * skipNode()). Note that, even though a rule marked as skipNode() does not
     * create a parse tree node of its own and is therefore "invisible" in the
     * parse tree, the rule still exists as a regular rule in the rule tree and
     * is accompanied by a "regular" rule {@link Context} during rule matching.
     * Corresponds to the {@link SkipNode} annotation.
     *
     * @return this Rule
     */
    Rule skipNode();

    /**
     * Tells whether this rule can match an empty input text
     *
     * <p>This method is used during rule building in other to detect anomalous
     * situations (for instance, the joining rule of a {@link JoinMatcher} is
     * empty).</p>
     *
     * @return true if the rule can match an empty input text
     */
    boolean canMatchEmpty();
}
