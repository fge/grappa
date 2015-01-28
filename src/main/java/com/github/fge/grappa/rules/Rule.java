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
