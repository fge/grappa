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
