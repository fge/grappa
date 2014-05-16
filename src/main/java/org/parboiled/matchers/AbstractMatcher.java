/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package org.parboiled.matchers;

import com.github.parboiled1.grappa.annotations.WillBeFinal;
import com.github.parboiled1.grappa.annotations.WillBeProtected;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.trees.ImmutableGraphNode;

/**
 * Abstract base class of most regular {@link Matcher}s.
 */
public abstract class AbstractMatcher
    extends ImmutableGraphNode<Matcher>
    implements Matcher, Cloneable
{
    private String label;
    private boolean nodeSuppressed;
    private boolean subnodesSuppressed;
    private boolean nodeSkipped;
    private Object tag;

    @WillBeProtected(version = "1.1.0")
    public AbstractMatcher(final String label)
    {
        this(new Rule[0], label);
    }

    @WillBeProtected(version = "1.1.0")
    public AbstractMatcher(final Rule subRule, final String label)
    {
        this(new Rule[]{ Preconditions.checkNotNull(subRule, "subRule") },
            label);
    }

    @WillBeProtected(version = "1.1.0")
    public AbstractMatcher(final Rule[] subRules, final String label)
    {
        super(ImmutableList.copyOf(
                toMatchers(Preconditions.checkNotNull(subRules))
            )
        );
        this.label = label;
    }

    private static Matcher[] toMatchers(final Rule... subRules)
    {
        final Matcher[] matchers = new Matcher[subRules.length];
        for (int i = 0; i < subRules.length; i++)
            matchers[i] = (Matcher) subRules[i];
        return matchers;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public boolean isNodeSuppressed()
    {
        return nodeSuppressed;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public boolean areSubnodesSuppressed()
    {
        return subnodesSuppressed;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public boolean isNodeSkipped()
    {
        return nodeSkipped;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public boolean areMismatchesMemoed()
    {
        return false;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public String getLabel()
    {
        return label;
    }

    @Override
    public boolean hasCustomLabel()
    {
        // this is the default implementation for single character matchers
        // complex matchers override with a custom implementation
        return true;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public String toString()
    {
        return getLabel();
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public AbstractMatcher label(final String label)
    {
        if (Objects.equal(label, this.label))
            return this;
        final AbstractMatcher clone = createClone();
        clone.label = label;
        return clone;
    }

    @Override
    public Rule suppressNode()
    {
        if (nodeSuppressed)
            return this;
        final AbstractMatcher clone = createClone();
        clone.nodeSuppressed = true;
        return clone;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public Rule suppressSubnodes()
    {
        if (subnodesSuppressed)
            return this;
        final AbstractMatcher clone = createClone();
        clone.subnodesSuppressed = true;
        return clone;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public Rule skipNode()
    {
        if (nodeSkipped)
            return this;
        final AbstractMatcher clone = createClone();
        clone.nodeSkipped = true;
        return clone;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public Rule memoMismatches()
    {
        return new MemoMismatchesMatcher(this);
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public Object getTag()
    {
        return tag;
    }

    @Override
    @WillBeFinal(version = "1.1.0")
    public void setTag(final Object tagObject)
    {
        tag = tagObject;
    }

    // default implementation is to simply delegate to the context
    @Override
    public <V> MatcherContext<V> getSubContext(final MatcherContext<V> context)
    {
        return context.getSubContext(this);
    }

    // creates a shallow copy
    private AbstractMatcher createClone()
    {
        try {
            return (AbstractMatcher) clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

}

