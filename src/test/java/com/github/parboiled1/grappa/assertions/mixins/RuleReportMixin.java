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
import com.github.parboiled1.grappa.matchers.base.Matcher;

public abstract class RuleReportMixin
{
//    @JsonDeserialize(as = DummyMatcher.class)
//    private Matcher matcher;
    @JsonProperty
    private int matches;
    @JsonProperty
    private int matchSubs;
    @JsonProperty
    private int mismatches;
    @JsonProperty
    private int mismatchSubs;
    @JsonProperty
    private int rematches;
    @JsonProperty
    private int rematchSubs;
    @JsonProperty
    private int remismatches;
    @JsonProperty
    private int remismatchSubs;
    @JsonIgnore
    private long nanoTime;

    @JsonCreator
    protected RuleReportMixin(@JsonProperty("matcher") final Matcher matcher)
    {
    }

    @JsonIgnore
    public Matcher getMatcher()
    {
        return null;
    }


    @JsonIgnore
    public int getInvocations()
    {
        return matches + mismatches;
    }

    @JsonIgnore
    public int getInvocationSubs()
    {
        return matchSubs + mismatchSubs;
    }

    @JsonIgnore
    public double getMatchShare()
    {
        return (double) matches / (double) getInvocations();
    }

    @JsonIgnore
    public double getMatchShare2()
    {
        return (double) matchSubs / (double) getInvocationSubs();
    }

    @JsonIgnore
    public int getReinvocations()
    {
        return rematches + remismatches;
    }

    @JsonIgnore
    public int getReinvocationSubs()
    {
        return rematchSubs + remismatchSubs;
    }

    @JsonIgnore
    public double getReinvocationShare()
    {
        return (double) getReinvocations() / (double) getInvocations();
    }

    @JsonIgnore
    public double getReinvocationShare2()
    {
        return (double) getReinvocationSubs() / (double) getInvocationSubs();
    }
}
