package com.github.parboiled1.grappa.assertions;

import org.assertj.core.api.SoftAssertions;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.parboiled.parserunners.ProfilingParseRunner.Report;

@ParametersAreNonnullByDefault
public final class ProfilingReportSoftAssert
{
    private final SoftAssertions soft;
    private final Report report;

    public ProfilingReportSoftAssert(final SoftAssertions soft,
        final Report report)
    {
        this.soft = soft;
        this.report = report;
    }

    public ProfilingReportSoftAssert hasTotalRuns(final int expected)
    {
        soft.assertThat(report.totalRuns)
            .as("profiling report: total runs")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasMatches(final int expected)
    {
        soft.assertThat(report.totalMatches)
            .as("profiling report: number of matches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasMismatches(final int expected)
    {
        soft.assertThat(report.totalMismatches)
            .as("profiling report: number of mismatches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasRematches(final int expected)
    {
        soft.assertThat(report.rematches)
            .as("profiling report: number of rematches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasRemismatches(final int expected)
    {
        soft.assertThat(report.remismatches)
            .as("profiling report: number of remismatches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasNumberOfRuleReports(final int expected)
    {
        soft.assertThat(report.ruleReports)
            .as("profiling report: number of rule reports")
            .hasSize(expected);
        return this;
    }

    public ProfilingReportSoftAssert matchesReport(final Report other)
    {
        return hasTotalRuns(other.totalRuns)
            .hasMatches(other.totalMatches)
            .hasMismatches(other.totalMismatches)
            .hasRematches(other.rematches)
            .hasRemismatches(other.remismatches)
            .hasNumberOfRuleReports(other.ruleReports.size());
    }
}
