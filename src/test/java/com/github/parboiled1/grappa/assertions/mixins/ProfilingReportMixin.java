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

package com.github.parboiled1.grappa.assertions.mixins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static org.parboiled.parserunners.ProfilingParseRunner.RuleReport;

public abstract class ProfilingReportMixin
{
    @JsonProperty("runs")
    private int totalRuns;
    @JsonIgnore
    private int totalInvocations;
    @JsonProperty("matches")
    private int totalMatches;
    @JsonProperty("mismatches")
    private int totalMismatches;
    @JsonIgnore
    private double matchShare;
    @JsonIgnore
    private int reinvocations;
    @JsonProperty("rematches")
    private int rematches;
    @JsonProperty("remismatches")
    private int remismatches;
    @JsonIgnore
    private double reinvocationShare;
    @JsonIgnore
    private long totalNanoTime;
    @JsonProperty("ruleReports")
    private List<RuleReport> ruleReports;

    @JsonCreator
    protected ProfilingReportMixin(
        @JsonProperty("runs") final int totalRuns,
        @JsonProperty("matches") final int totalMatches,
        @JsonProperty("mismatches") final int totalMismatches,
        @JsonProperty("rematches") final int rematches,
        @JsonProperty("remismatches") final int remismatches,
        @JsonProperty("totalNanoTime") final long totalNanoTime,
        @JsonProperty("ruleReports") final List<RuleReport> ruleReports
    )
    {
    }
}
