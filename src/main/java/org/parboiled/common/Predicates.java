/*
 * Copyright (C) 2007 Google Inc., adapted in 2010 by Mathias Doenitz
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

package org.parboiled.common;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * DEPRECATED!
 *
 * @deprecated use Guava's {@link com.google.common.base.Predicates} instead
 */
@Deprecated
public final class Predicates {

    private static final Joiner COMMA = Joiner.on(", ");

    private Predicates() {}

    /**
     * Returns a predicate that always evaluates to {@code true}.
     *
     * @return a predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTrue() {
        return (Predicate<T>) AlwaysTruePredicate.INSTANCE;
    }

    /**
     * Returns a predicate that always evaluates to {@code false}.
     *
     * @return a predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalse() {
        return (Predicate<T>) AlwaysFalsePredicate.INSTANCE;
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference
     * being tested is null.
     *
     * @return a predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> isNull() {
        return (Predicate<T>) IsNullPredicate.INSTANCE;
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference
     * being tested is not null.
     *
     * @return a predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> notNull() {
        return (Predicate<T>) NotNullPredicate.INSTANCE;
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the given predicate
     * evaluates to {@code false}.
     *
     * @param predicate the inner predicate
     * @return a predicate
     */
    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return new NotPredicate<T>(predicate);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if each of its
     * components evaluates to {@code true}. The components are evaluated in
     * order, and evaluation will be "short-circuited" as soon as a false
     * predicate is found. It defensively copies the iterable passed in, so future
     * changes to it won't alter the behavior of this predicate. If {@code
     * components} is empty, the returned predicate will always evaluate to {@code
     * true}.
     *
     * @param components the components
     * @return a predicate
     */
    public static <T> Predicate<T> and(final Collection<? extends Predicate<? super T>> components) {
        return new AndPredicate<T>(components);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if each of its
     * components evaluates to {@code true}. The components are evaluated in
     * order, and evaluation will be "short-circuited" as soon as a false
     * predicate is found. It defensively copies the array passed in, so future
     * changes to it won't alter the behavior of this predicate. If {@code
     * components} is empty, the returned predicate will always evaluate to {@code
     * true}.
     *
     * @param components the components
     * @return a predicate
     */
    public static <T> Predicate<T> and(final Predicate<? super T>... components) {
        return new AndPredicate<T>(ImmutableList.copyOf(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if both of its
     * components evaluate to {@code true}. The components are evaluated in
     * order, and evaluation will be "short-circuited" as soon as a false
     * predicate is found.
     *
     * @param first the first
     * @param second the second
     * @return a predicate
     */
    public static <T> Predicate<T> and(
        final Predicate<? super T> first, final Predicate<? super T> second) {
        return new AndPredicate<T>(
            ImmutableList.<Predicate<? super T>>of(first, second)
        );
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its
     * components evaluates to {@code true}. The components are evaluated in
     * order, and evaluation will be "short-circuited" as soon as as soon as a
     * true predicate is found. It defensively copies the iterable passed in, so
     * future changes to it won't alter the behavior of this predicate. If {@code
     * components} is empty, the returned predicate will always evaluate to {@code
     * false}.
     *
     * @param components the components
     * @return a predicate
     */
    public static <T> Predicate<T> or(final Collection<? extends Predicate<? super T>> components) {
        return new OrPredicate<T>(components);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its
     * components evaluates to {@code true}. The components are evaluated in
     * order, and evaluation will be "short-circuited" as soon as as soon as a
     * true predicate is found. It defensively copies the array passed in, so
     * future changes to it won't alter the behavior of this predicate. If {@code
     * components} is empty, the returned predicate will always evaluate to {@code
     * false}.
     *
     * @param components the components
     * @return a predicate
     */
    public static <T> Predicate<T> or(final Predicate<? super T>... components) {
        return new OrPredicate<T>(
            ImmutableList.copyOf(components)
        );
    }

    /**
     * Returns a predicate that evaluates to {@code true} if either of its
     * components evaluates to {@code true}. The components are evaluated in
     * order, and evaluation will be "short-circuited" as soon as as soon as a
     * true predicate is found.
     *
     * @param first the first
     * @param second the second
     * @return a predicate
     */
    public static <T> Predicate<T> or(
        final Predicate<? super T> first, final Predicate<? super T> second) {
        return new OrPredicate<T>(ImmutableList.<Predicate<? super T>>of(first, second));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object being
     * tested {@code equals()} the given target or both are null.
     *
     * @param target the target
     * @return a predicate
     */
    public static <T> Predicate<T> equalTo(final T target) {
        return (target == null) ? Predicates.<T>isNull() : new IsEqualToPredicate<T>(target);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object being
     * tested is an instance of the given class. If the object being tested
     * is {@code null} this predicate evaluates to {@code false}.
     *
     *
     * @param clazz the clazz
     * @return a predicate
     */
    public static Predicate<Object> instanceOf(final Class<?> clazz) {
        return new InstanceOfPredicate(clazz);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference
     * being tested is a member of the given collection. It does not defensively
     * copy the collection passed in, so future changes to it will alter the
     * behavior of the predicate.
     *
     * This method can technically accept any Collection&lt;?&gt;,
     * but using a typed
     * collection helps prevent bugs. This approach doesn't block any potential
     * users since it is always possible to use {@code Predicates.<Object>in()}.
     *
     * @param target the collection that may contain the function input
     * @return a predicate
     */
    public static <T> Predicate<T> in(final Collection<? extends T> target) {
        return new InPredicate<T>(target);
    }

    private static class AlwaysTruePredicate implements Predicate<Object> {
        private static final Predicate<Object> INSTANCE = new AlwaysTruePredicate();

        @Override
        public boolean apply(final Object o) {
            return true;
        }

        @Override
        public
        String toString() {
            return "AlwaysTrue";
        }
    }

    private static class AlwaysFalsePredicate implements Predicate<Object> {
        private static final Predicate<Object> INSTANCE = new AlwaysFalsePredicate();

        @Override
        public boolean apply(final Object o) {
            return false;
        }

        @Override
        public String toString() {
            return "AlwaysFalse";
        }
    }

    private static class NotPredicate<T> implements Predicate<T> {
        private final Predicate<T> predicate;

        private NotPredicate(final Predicate<T> predicate) {
            checkArgNotNull(predicate, "predicate");
            this.predicate = predicate;
        }

        @Override
        public boolean apply(final T t) {
            return !predicate.apply(t);
        }

        public String toString() {
            return "Not(" + predicate.toString() + ")";
        }
    }

    private static class AndPredicate<T> implements Predicate<T> {
        private final Collection<? extends Predicate<? super T>> components;

        private AndPredicate(final Collection<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean apply(final T t) {
            for (final Predicate<? super T> predicate : components) {
                if (!predicate.apply(t)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return "And(" + COMMA.join(components) + ")";
        }
    }

    private static class OrPredicate<T> implements Predicate<T> {
        private final Collection<? extends Predicate<? super T>> components;

        private OrPredicate(final Collection<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean apply(final T t) {
            for (final Predicate<? super T> predicate : components) {
                if (predicate.apply(t)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "Or(" + COMMA.join(components) + ")";
        }
    }

    private static class IsEqualToPredicate<T> implements Predicate<T> {
        private final T target;

        private IsEqualToPredicate(final T target) {
            this.target = target;
        }

        @Override
        public boolean apply(final T t) {
            return target.equals(t);
        }

        @Override
        public String toString() {
            return "IsEqualTo(" + target + ")";
        }
    }

    private static class InstanceOfPredicate implements Predicate<Object> {
        private final Class<?> clazz;

        private InstanceOfPredicate(final Class<?> clazz) {
            checkArgNotNull(clazz, "clazz");
            this.clazz = clazz;
        }

        @Override
        public boolean apply(final Object o) {
            return clazz.isInstance(o);
        }

        @Override
        public String toString() {
            return "IsInstanceOf(" + clazz.getName() + ")";
        }
    }

    private static class IsNullPredicate implements Predicate<Object> {
        private static final Predicate<Object> INSTANCE = new IsNullPredicate();

        @Override
        public boolean apply(final Object o) {
            return o == null;
        }

        @Override
        public String toString() {
            return "IsNull";
        }
    }

    private static class NotNullPredicate implements Predicate<Object> {
        private static final Predicate<Object> INSTANCE = new NotNullPredicate();

        @Override
        public boolean apply(final Object o) {
            return o != null;
        }

        @Override
        public String toString() {
            return "NotNull";
        }
    }

    private static class InPredicate<T> implements Predicate<T> {
        private final Collection<?> target;

        private InPredicate(final Collection<?> target) {
            checkArgNotNull(target, "target");
            this.target = target;
        }

        @Override
        public boolean apply(final T t) {
            try {
                return target.contains(t);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "In(" + target + ")";
        }
    }

}