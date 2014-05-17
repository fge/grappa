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

package com.github.parboiled1.grappa.helpers;

import com.github.parboiled1.grappa.annotations.Experimental;
import com.github.parboiled1.grappa.event.EventBusParser;

import javax.annotation.Nonnull;

/**
 * A value builder for use in a parser
 *
 * <p>Note that all setters should return a {@code boolean}, since the primary
 * use of this interface is within rules. If the grammar rules are not enough
 * for validation, you may make the setter return {@code false} to signal a
 * parsing error.</p>
 *
 * <p>This is also the base interface used by {@link EventBusParser} to post
 * events.</p>
 *
 * @param <T> type of the value produced
 *
 * @see EventBusParser#post(ValueBuilder)
 */
@Experimental
public interface ValueBuilder<T>
{
    /**
     * Build the value
     *
     * @return the built value
     */
    @Nonnull
    T build();

    /**
     * Reset this builder to a pristine state
     *
     * <p>When this method has been called, all injected values are lost and
     * cannot be relied upon anymore.</p>
     *
     * <p>Note that this interface makes no guarantee as to how {@link #build()}
     * behaves with an empty value builder.</p>
     *
     * @return always {@code true}
     */
    boolean reset();
}
