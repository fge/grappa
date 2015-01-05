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

package com.github.parboiled1.grappa.testparsers;

import org.parboiled.Rule;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.Reference;

public class PrevCallsParser
    extends TestParser<Integer>
{
    @Override
    @SuppressSubnodes
    public Rule mainRule()
    {
        final Reference<Integer> a = new Reference<>();
        final Reference<Character> op = new Reference<>();
        final Reference<Integer> b = new Reference<>();
        return sequence(
            digits(), a.set(pop()),
            operator(), op.set(matchedChar()),
            digits(), b.set(pop()),
            EOI,
            push(op.get() == '+' ? a.get() + b.get() : a.get() - b.get()));
    }

    public Rule operator()
    {
        return firstOf('+', '-');
    }

    public Rule digits()
    {
        return sequence(digits2(), debug());
    }

    boolean debug()
    {
        return true;
    }

    public Rule digits2()
    {
        return sequence(oneOrMore(digit()), push(Integer.parseInt(match())));
    }
}
