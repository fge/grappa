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

package org.parboiled.support;

import com.github.fge.grappa.annotations.DoNotUse;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.parboiled.common.Factory;
import org.parboiled.common.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;

/**
 * <p>This class provides a "local variable"-like construct for action
 * expressions in parser rule methods.</p>
 *
 * <p>Var objects wrap an internal value of an arbitrary (reference) type, can
 * have an initial value, allow read/write access to their values and can be
 * passed around as parameters to nested rule methods.</p>
 *
 * <p>Each rule invocation (i.e. rule matching attempt) receives its own
 * scope (which is automatically initialized with the initial value), so actions
 * in recursive rules work just like expected.</p>
 *
 * <p>Var objects generally behave just like local variables with one exception:
 * When rule method {@code rule1()} passes a Var defined in its scope to another
 * rule method {@code rule2()} as a parameter, and an action in rule method
 * {@code rule2()} writes to this Var, all actions in rule method {@code
 * rule1()} running after {@code rule2()} will "see" this newly written value
 * (since values in Var objects are passed by reference).</p>
 *
 * @param <T> the type wrapped by this Var
 */
public class Var<T>
    extends Reference<T>
{
    private final Factory<T> factory;
    private final Deque<T> stack = Lists.newLinkedList();
    private int level;
    private String name;

    /**
     * Initializes a new Var with a null initial value.
     */
    // TODO: disallow
    public Var()
    {
        this((T) null);
    }

    /**
     * Initializes a new Var with the given initial value.
     *
     * @param value the value
     */
    public Var(@Nullable final T value)
    {
        super(value);
        factory = new Factory<T>()
        {
            @Nullable
            @Override
            public T create()
            {
                return value;
            }
        };
    }

    /**
     * Initializes a new Var. The given factory will be used to create the
     * initial value for each "execution frame" of the enclosing rule.
     *
     * @param factory the factory used to create the initial value for a rule execution frame
     *
     */
    public Var(@Nonnull final Factory<T> factory)
    {
        this.factory = Preconditions.checkNotNull(factory);
    }

    /**
     * Gets the name of this Var.
     *
     * @return the name
     */
    public final String getName()
    {
        return name;
    }

    /**
     * Sets the name of this Var.
     *
     * @param name the name
     */
    public final void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the current frame level of this variable, the very first level
     * corresponding to zero.
     *
     * @return the current level
     */
    public final int getLevel()
    {
        return level;
    }

    /**
     * Provides a new frame for the variable.
     * Potentially existing previous frames are saved.
     * Normally you do not have to call this method manually as parboiled
     * provides for automatic Var frame management.
     *
     * @return true
     */
    @DoNotUse
    public final boolean enterFrame()
    {
        if (level++ > 0)
            stack.add(get());
        return set(factory.create());
    }

    /**
     * Exits a frame previously entered with {@link #enterFrame()}.
     * Normally you do not have to call this method manually as parboiled
     * provides for automatic Var frame management.
     *
     * @return true
     */
    @DoNotUse
    public final boolean exitFrame()
    {
        if (--level > 0)
            set(stack.removeLast());
        return true;
    }

    @Override
    public final String toString()
    {
        return Optional.fromNullable(name).or(super.toString());
    }
}
