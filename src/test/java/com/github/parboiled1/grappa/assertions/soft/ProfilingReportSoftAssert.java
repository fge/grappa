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

package com.github.parboiled1.grappa.assertions.soft;

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
        soft.assertThat(report.getTotalRuns())
            .as("profiling report: total runs")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasMatches(final int expected)
    {
        soft.assertThat(report.getTotalMatches())
            .as("profiling report: number of matches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasMismatches(final int expected)
    {
        soft.assertThat(report.getTotalMismatches())
            .as("profiling report: number of mismatches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasRematches(final int expected)
    {
        soft.assertThat(report.getRematches())
            .as("profiling report: number of rematches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasRemismatches(final int expected)
    {
        soft.assertThat(report.getRemismatches())
            .as("profiling report: number of remismatches")
            .isEqualTo(expected);
        return this;
    }

    public ProfilingReportSoftAssert hasNumberOfRuleReports(final int expected)
    {
        soft.assertThat(report.getRuleReports())
            .as("profiling report: number of rule reports")
            .hasSize(expected);
        return this;
    }

    public ProfilingReportSoftAssert matchesReport(final Report other)
    {
        return hasTotalRuns(other.getTotalRuns())
            .hasMatches(other.getTotalMatches())
            .hasMismatches(other.getTotalMismatches())
            .hasRematches(other.getRematches())
            .hasRemismatches(other.getRemismatches())
            .hasNumberOfRuleReports(other.getRuleReports().size());
    }
}
