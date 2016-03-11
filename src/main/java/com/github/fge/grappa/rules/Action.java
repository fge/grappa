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

import com.github.fge.grappa.parsers.BaseActions;
import com.github.fge.grappa.run.context.Context;
import com.github.fge.grappa.run.context.ContextAware;

/**
 * Class for a user defined action
 *
 * <p>Instances of classes implementing this interface can be used directly in a
 * rule definition to define a parser action. In addition, all boolean
 * expressions and methods returning a boolean in parsers will be transformed so
 * as to implement this interface.</p>
 *
 * <p>If a user implementation of this class also implements {@link
 * ContextAware}, instances will be made aware of the current parsing context
 * (via {@link ContextAware#setContext(Context)} immediately before the
 * invocation of the {@link #run} method.</p>
 *
 * <p>Additionally, if the class implementing this interface is an inner class
 * (anonymous or not) and its outer class(es) implement(s) {@link ContextAware},
 * its outer class(es) will also be informed of the current parsing context,
 * immediately before the invocation of the action's {@link * #run} method.</p>
 *
 * <p>This allows simple anonymous action class implementations directly in the
 * parser rule definitions, even when they access context-sensitive methods
 * defined in parser classes.</p>
 *
 * @see BaseActions#match()
 */
public interface Action<V>
{
    /**
     * Runs the parser action.
     *
     * @param context the current parsing context
     * @return true if the parsing process is to proceed, false if the current
     * rule is to fail
     */
    boolean run(Context<V> context);
}
