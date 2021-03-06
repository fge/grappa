/*
 * Copyright (c) 2009-2011 Ken Wenzel and Mathias Doenitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.fge.grappa.transform.runtime;

import com.github.fge.grappa.rules.SkippableAction;

import java.util.Objects;

/**
 * Base class of generated classes wrapping action expressions.
 */
public abstract class BaseAction<V>
    extends BaseGroupClass
    implements SkippableAction<V>
{
    private boolean skipInPredicates;

    protected BaseAction(final String name)
    {
        super(Objects.requireNonNull(name, "name"));
    }

    @Override
    public boolean skipInPredicates()
    {
        return skipInPredicates;
    }

    public void setSkipInPredicates()
    {
        skipInPredicates = true;
    }
}
