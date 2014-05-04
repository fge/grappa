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

import com.github.parboiled1.grappa.cleanup.DoNotUse;
import com.github.parboiled1.grappa.cleanup.WillBeFinal;
import com.google.common.base.Preconditions;
import org.parboiled.common.Factory;
import org.parboiled.common.Reference;

import javax.annotation.Nonnull;
import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>This class provides a "local variable"-like construct for action expressions in parser rule methods.
 * Var objects wrap an internal value of an arbitrary (reference) type, can have an initial value,
 * allow read/write access to their values and can be passed around as parameters to nested rule methods.
 * Each rule invocation (i.e. rule matching attempt) receives its own Var scope (which is automatically
 * initialized with the initial value), so actions in recursive rules work just like expected.</p>
 * <p>Var objects generally behave just like local variables with one exception:
 * When rule method A() passes a Var defined in its scope to another rule method B() as a parameter and an action
 * in rule method B() writes to this Var all actions in rule method A() running after B() will "see" this newly written
 * value (since values in Var objects are passed by reference)</p>
 *
 * @param <T> the type wrapped by this Var
 */
public class Var<T>
    extends Reference<T>
{

    private final Factory<T> factory;
    // TODO: can be null :(
    private Deque<T> stack;
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
    public Var(final T value)
    {
        super(value);
        factory = new Factory<T>()
        {
            @Override
            public T create()
            {
                return value;
            }
        };
    }

    /**
     * Initializes a new Var. The given factory will be used to create the initial value for each "execution frame"
     * of the enclosing rule.
     *
     * @param factory the factory used to create the initial value for a rule execution frame
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
    @WillBeFinal(version = "1.1")
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this Var.
     *
     * @param name the name
     */
    @WillBeFinal(version = "1.1")
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the current frame level of this variable, the very first level corresponding to zero.
     *
     * @return the current level
     */
    @WillBeFinal(version = "1.1")
    public int getLevel()
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
    @WillBeFinal(version = "1.1")
    public boolean enterFrame()
    {
        if (level++ > 0) {
            if (stack == null)
                stack = new LinkedList<T>();
            stack.add(get());
        }
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
    @WillBeFinal(version = "1.1")
    public boolean exitFrame()
    {
        if (--level > 0)
            set(stack.removeLast());
        return true;
    }

    @Override
    public String toString()
    {
        return name != null ? name : super.toString();
    }
}
