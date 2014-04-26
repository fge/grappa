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

package org.parboiled.annotations;

import org.parboiled.Context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Do not create a parse tree node for this rule
 *
 * <p>When building a parsing tree, all rules will generate a parsing node by
 * default. This annotation can be used on a rule to prevent the creation of a
 * node for this particular rule <strong>only</strong> (this means the
 * <em>children</em> of this rule will still see their nodes created).</p>
 *
 * <p>For instance, this set of rules:</p>
 *
 * <pre>
 *     &#x0040;SkipNode
 *     Rule ab()
 *     {
 *         return Sequence(a(), b());
 *     }
 *
 *     Rule a() {...}
 *
 *     Rule b() {...}
 *
 * </pre>
 *
 * <p>will generate nodes for {@code a} and {@code b} but not for {@code ab}.
 * </p>
 *
 * <p>Note however that such rules still have a {@link Context} available.</p>
 *
 * @see SuppressNode
 * @see SuppressSubnodes
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SkipNode
{
}