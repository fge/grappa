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

import java.util.List;

/**
 * Special wrapping matcher that performs memoization of the last mismatch of the wrapped sub rule.
 */
public class MemoMismatchesMatcher implements Matcher {
    private final Matcher inner;

    public MemoMismatchesMatcher(final Rule inner) {
        this.inner = Preconditions.checkNotNull((Matcher) inner, "inner");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> boolean match(final MatcherContext<V> context) {
        if (context.hasMismatched()) {
            return false;
        }
        if (inner.match(context)) {
            return true;
        }
        context.memoizeMismatch();
        return false;
    }

    // GraphNode

    @Override
    public List<Matcher> getChildren() {
        return inner.getChildren();
    }

    // Rule

    @Override
    public Rule label(final String label) {
        return new MemoMismatchesMatcher(inner.label(label));
    }

    @Override
    public Rule suppressNode() {
        return new MemoMismatchesMatcher(inner.suppressNode());
    }

    @Override
    public Rule suppressSubnodes() {
        return new MemoMismatchesMatcher(inner.suppressSubnodes());
    }

    @Override
    public Rule skipNode() {
        return new MemoMismatchesMatcher(inner.skipNode());
    }

    @Override
    public Rule memoMismatches() {
        return this; // already done
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
    public boolean areMismatchesMemoed() { return true; }

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
     * Retrieves the innermost Matcher that is not a MemoMismatchesMatcher.
     *
     * @param matcher the matcher to unwrap
     * @return the given instance if it is not a MemoMismatchesMatcher, otherwise the innermost Matcher
     */
    public static Matcher unwrap(final Matcher matcher) {
        if (matcher instanceof MemoMismatchesMatcher) {
            final MemoMismatchesMatcher memoMismatchesMatcher = (MemoMismatchesMatcher) matcher;
            return unwrap(memoMismatchesMatcher.inner);
        }
        return matcher;
    }
}