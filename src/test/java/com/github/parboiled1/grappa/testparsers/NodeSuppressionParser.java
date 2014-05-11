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
import org.parboiled.annotations.Label;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

public class NodeSuppressionParser
    extends TestParser<Object>
{
    @Override
    @Label("abcdefgh")
    public Rule mainRule()
    {
        return sequence(abcd(), efgh());
    }

    public Rule abcd()
    {
        return sequence(ab(), cd());
    }

    public Rule efgh()
    {
        return sequence(ef(), gh());
    }

    public Rule ab()
    {
        return sequence(a(), b());
    }

    @SuppressSubnodes
    public Rule cd()
    {
        return sequence(c(), d());
    }

    public Rule ef()
    {
        return sequence(e(), f());
    }

    public Rule gh()
    {
        return sequence(g(), h()).suppressNode();
    }

    public Rule a()
    {
        return ch('a');
    }

    @SuppressNode
    public Rule b()
    {
        return ch('b');
    }

    public Rule c()
    {
        return ch('c');
    }

    public Rule d()
    {
        return ch('d');
    }

    public Rule e()
    {
        return ch('e').suppressSubnodes();
    }

    public Rule f()
    {
        return ch('f').suppressNode();
    }

    public Rule g()
    {
        return ch('g');
    }

    public Rule h()
    {
        return ch('h');
    }
}
