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

package com.github.parboiled1.grappa.statistics;

import com.github.parboiled1.grappa.testparsers.VarFramingParser;

import java.io.IOException;

public final class VarFramingStatisticsTest
    extends ParserStatisticsTest<VarFramingParser, Integer>
{

    public VarFramingStatisticsTest()
        throws IOException
    {
        super(VarFramingParser.class, "varFraming.json");
    }
}