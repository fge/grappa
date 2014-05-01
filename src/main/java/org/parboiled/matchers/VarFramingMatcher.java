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

import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.matchervisitors.MatcherVisitor;
import org.parboiled.support.Var;

import java.util.List;

/**
 * Special wrapping matcher that manages the creation and destruction of execution frames for a number of action vars.
 */
public class VarFramingMatcher implements Matcher {
    private final Matcher inner;
    private final Var<?>[] variables;

    public VarFramingMatcher(final Rule inner, final Var<?>[] variables) {
        this.inner = Preconditions.checkNotNull((Matcher) inner, "inner");
        this.variables = Preconditions.checkNotNull(variables, "variables");
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context) {
        for (final Var<?> var : variables) {
            var.enterFrame();
        }

        final boolean matched = inner.match(context);

        for (final Var<?> var : variables) {
            var.exitFrame();
        }

        return matched;
    }

    // GraphNode

    @Override
    public List<Matcher> getChildren() {
        return inner.getChildren();
    }

    // Rule

    @Override
    public Rule label(final String label) {
        return new VarFramingMatcher(inner.label(label), variables);
    }

    @Override
    public Rule suppressNode() {
        return new VarFramingMatcher(inner.suppressNode(), variables);
    }

    @Override
    public Rule suppressSubnodes() {
        return new VarFramingMatcher(inner.suppressSubnodes(), variables);
    }

    @Override
    public Rule skipNode() {
        return new VarFramingMatcher(inner.skipNode(), variables);
    }

    @Override
    public Rule memoMismatches() {
        return new VarFramingMatcher(inner.memoMismatches(), variables);
    }

    // Matcher

    @Override
    public String getLabel() {return inner.getLabel();}

    @Override
    public boolean hasCustomLabel() {return inner.hasCustomLabel();}

    @Override
    public boolean isNodeSuppressed() {return inner.isNodeSuppressed();}

    @Override
    public boolean areSubnodesSuppressed() {return inner.areSubnodesSuppressed();}

    @Override
    public boolean isNodeSkipped() {return inner.isNodeSkipped();}

    @Override
    public boolean areMismatchesMemoed() { return inner.areMismatchesMemoed(); }

    @Override
    public void setTag(final Object tagObject) { inner.setTag(tagObject); }

    @Override
    public Object getTag() { return inner.getTag(); }
    
    @Override
    public <V> MatcherContext<V> getSubContext(final MatcherContext<V> context) {
        final MatcherContext<V> subContext = inner.getSubContext(context);
        subContext.setMatcher(this); // we need to inject ourselves here otherwise we get cut out
        return subContext;
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor) {
        Preconditions.checkNotNull(visitor, "visitor");
        return inner.accept(visitor);
    }

    @Override
    public String toString() { return inner.toString(); }

    /**
     * Retrieves the innermost Matcher that is not a VarFramingMatcher.
     *
     * @param matcher the matcher to unwrap
     * @return the given instance if it is not a VarFramingMatcher, otherwise the innermost Matcher
     */
    public static Matcher unwrap(final Matcher matcher) {
        if (matcher instanceof VarFramingMatcher) {
            final VarFramingMatcher varFramingMatcher = (VarFramingMatcher) matcher;
            return unwrap(varFramingMatcher.inner);
        }
        return matcher;
    }

}