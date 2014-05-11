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

package com.github.parboiled1.grappa.parsers;

import com.github.parboiled1.grappa.matchers.join.JoinMatcher;
import com.github.parboiled1.grappa.matchers.join.JoinMatcherBootstrap;
import com.google.common.annotations.Beta;
import org.parboiled.BaseParser;

/**
 * Base parser with a {@code join()} method to create a {@link JoinMatcher}
 *
 * <p>Given two rules {@code rule} and {@code separator}, here are sample
 * usages:</p>
 *
 * <pre>
 *     Rule myRule()
 *     {
 *         // Minimum twice
 *         return join(rule).using(separator).min(2);
 *         // Maximum eight times
 *         return join(rule).using(separator).max(8);
 *         // Exactly twice
 *         return join(rule).using(separator).times(2);
 *         // At least 3, at most 5
 *         return join(rule).using(separator).times(3, 5);
 *         // Advanced: using a Guava Range
 *         return join(rule).using(separator).range(Range.atLeast(8));
 *     }
 * </pre>
 *
 * @param <V> production value of this parser
 */
@Beta
public abstract class JoinParser<V>
    extends BaseParser<V>
{
    public final JoinMatcherBootstrap<V, BaseParser<V>> join(
        final Object joined)
    {
        return new JoinMatcherBootstrap<V, BaseParser<V>>(this, joined);
    }
}
