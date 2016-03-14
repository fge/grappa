/*
 * Copyright (C) 2015 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.grappa.run;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.rules.Rule;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The base {@link ParseRunner} implementation
 *
 * <p>This implementation allows you to attach {@link ParseEventListener}
 * instances; those must be {@link #registerListener(ParseEventListener)
 * registered} <em>before</em> you {@link #run(InputBuffer)} the parsing
 * process.</p>
 */
@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
@NonFinalForTesting
public class ListeningParseRunner<V>
    extends ParseRunner<V>
{
    /**
     * Creates a new BasicParseRunner instance for the given rule.
     *
     * @param rule the parser rule
     */
    public ListeningParseRunner(final Rule rule)
    {
        super(rule);
    }

}
