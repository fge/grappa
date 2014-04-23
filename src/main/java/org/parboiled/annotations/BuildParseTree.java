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

import org.parboiled.parserunners.RecoveringParseRunner;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Build a parse tree
 *
 * <p>This annotation is to be used on a parser class. When present, the
 * generated parsers will build a parse tree for their input.</p>
 *
 * <p>You can further control the tree building process by using annotations on
 * rules within the parser: namely, {@link SuppressSubnodes}, {@link SkipNode}
 * and {@link SuppressNode}. These annotations have no effect if this annotation
 * is not present.</p>
 *
 * <p>Note: if the input contains parse errors and you use a {@link
 * RecoveringParseRunner}, the generated parser will create parse tree nodes for
 * all rules that have recorded parse errors (note that this always includes
 * the root rule).</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BuildParseTree {
}