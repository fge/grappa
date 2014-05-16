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

package org.parboiled.common;

import com.github.parboiled1.grappa.annotations.WillBeRemoved;
import com.google.common.hash.PrimitiveSink;
import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;

/**
 * Deprecated!
 *
 * <p>This interface will have no replacement. Some solutions to it are:</p>
 *
 * <ul>
 *     <li>Guava's {@link PrimitiveSink};</li>
 *     <li>Guava's {@link ByteSink} or {@link CharSink}.</li>
 * </ul>
 *
 * @param <T> values to be consumed
 */
@Deprecated
@WillBeRemoved(version = "1.1")
public interface Sink<T>
{
    void receive(T value);
}
