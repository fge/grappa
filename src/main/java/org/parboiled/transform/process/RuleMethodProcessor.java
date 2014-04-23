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

package org.parboiled.transform.process;

import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;

/**
 * An individual processor for altering a generated {@link RuleMethod}
 *
 * <p>All processors defined make use of ASM to manipulate the byte code of
 * generated methods. A given processor may, or may not, apply to a given rule.
 * This depends on what annotations are used on the rule, whether it takes
 * arguments, etc etc.</p>
 */
public interface RuleMethodProcessor
{

    boolean appliesTo(ParserClassNode classNode, RuleMethod method);

    void process(ParserClassNode classNode, RuleMethod method)
        throws Exception;
}