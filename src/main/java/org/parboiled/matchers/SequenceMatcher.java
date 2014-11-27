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
 * A {@link Matcher} that executes all of its submatchers in sequence and only succeeds if all submatchers succeed.
 */
/*
 * TODO! Generalize, abstract away, something...
 *
 * The thing is, Actions can only occur within this class; this makes it
 * impossible to .chainInto(someMatchConsumer) for instance, which is a royal
 * pain.
 */
public class SequenceMatcher
    extends CustomDefaultLabelMatcher<SequenceMatcher>
{

    public SequenceMatcher(final Rule[] subRules)
    {
        super(Preconditions.checkNotNull(subRules, "subRules"), "sequence");
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        final Object valueStackSnapshot = context.getValueStack()
            .takeSnapshot();

        final List<Matcher> children = getChildren();
        final int size = children.size();
        for (int i = 0; i < size; i++) {
            final Matcher matcher = children.get(i);

            // remember the current index in the context, so we can access it
            // for building the current follower set
            context.setIntTag(i);

            if (!matcher.getSubContext(context).runMatcher()) {
                // rule failed, so invalidate all stack actions the rule might
                // have done
                context.getValueStack().restoreSnapshot(valueStackSnapshot);
                return false;
            }
        }
        context.createNode();
        return true;
    }

    @Override
    public <R> R accept(final MatcherVisitor<R> visitor)
    {
        Preconditions.checkNotNull(visitor, "visitor");
        return visitor.visit(this);
    }
}
