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

package com.github.parboiled1.grappa.trace;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.parboiled.MatcherContext;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class TraceEvent
{
    private final TraceEventType type;
    private final long nanoseconds;
    private final int index;
    private final String matcher;
    private final String path;
    private final int level;

    @JsonCreator
    TraceEvent(@JsonProperty("type") final TraceEventType type,
        @JsonProperty("nanoseconds") final long nanoseconds,
        @JsonProperty("index") final int index,
        @JsonProperty("matcher") final String matcher,
        @JsonProperty("path") final String path,
        @JsonProperty("level") final int level)
    {
        this.type = type;
        this.nanoseconds = nanoseconds;
        this.index = index;
        this.matcher = matcher;
        this.path = path;
        this.level = level;
    }

    @JsonIgnore
    public TraceEvent(final TraceEventType type,
        final MatcherContext<?> context)
    {
        nanoseconds = System.nanoTime();
        this.type = type;
        index = context.getCurrentIndex();
        // TODO: .getMatcher() normally never returns null
        matcher = context.getMatcher().toString();
        path = context.getPath().toString();
        level = context.getLevel();
    }

    public TraceEventType getType()
    {
        return type;
    }

    public long getNanoseconds()
    {
        return nanoseconds;
    }

    public int getIndex()
    {
        return index;
    }

    public String getMatcher()
    {
        return matcher;
    }

    public String getPath()
    {
        return path;
    }

    public int getLevel()
    {
        return level;
    }

    @Override
    @Nonnull
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("type", type)
            .add("nanoseconds", nanoseconds)
            .add("index", index)
            .add("matcher", matcher)
            .add("path", path)
            .add("level", level)
            .toString();
    }
}
