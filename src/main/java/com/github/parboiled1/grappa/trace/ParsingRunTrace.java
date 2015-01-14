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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

public final class ParsingRunTrace
{
    private final long startDate;
    private final List<TraceEvent> events;

    @JsonCreator
    public ParsingRunTrace(@JsonProperty("startDate") final long startDate,
        @JsonProperty("events") final List<TraceEvent> events)
    {
        this.startDate = startDate;
        this.events = ImmutableList.copyOf(events);
    }

    public long getStartDate()
    {
        return startDate;
    }

    public List<TraceEvent> getEvents()
    {
        return events;
    }
}
