package org.parboiled.transform.method;

import com.google.common.collect.ImmutableMap;
import org.objectweb.asm.Type;
import org.parboiled.annotations.Cached;
import org.parboiled.annotations.DontExtend;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.DontSkipActionsInPredicates;
import org.parboiled.annotations.ExplicitActionsOnly;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SkipActionsInPredicates;
import org.parboiled.annotations.SkipNode;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.transform.RuleMethod;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration associating parser or rule annotations to their ASM descriptor
 *
 * @see Type#getDescriptor()
 * @see org.parboiled.annotations
 */
public enum ParserAnnotation
{
    EXPLICIT_ACTIONS_ONLY(ExplicitActionsOnly.class),
    CACHED(Cached.class),
    SUPPRESS_NODE(SuppressNode.class),
    SUPPRESS_SUBNODES(SuppressSubnodes.class),
    SKIP_NODE(SkipNode.class),
    MEMO_MISMATCHES(MemoMismatches.class),
    SKIP_ACTIONS_IN_PREDICATES(SkipActionsInPredicates.class),
    DONT_SKIP_ACTIONS_IN_PREDICATES(DontSkipActionsInPredicates.class),
    DONT_LABEL(DontLabel.class),
    DONT_EXTEND(DontExtend.class)
    ;

    /**
     * @see RuleMethod#moveFlagsTo(RuleMethod)
     */
    private static final Set<ParserAnnotation> FLAGS_COPY = EnumSet.of(
        CACHED, DONT_LABEL, SUPPRESS_NODE, SUPPRESS_SUBNODES,
        SKIP_NODE, MEMO_MISMATCHES
    );

    /**
     * @see RuleMethod#moveFlagsTo(RuleMethod)
     */
    private static final Set<ParserAnnotation> FLAGS_CLEAR = EnumSet.of(
        CACHED, SUPPRESS_NODE, SUPPRESS_SUBNODES, SKIP_NODE,
        MEMO_MISMATCHES
    );

    /**
     * @see RuleMethod#moveFlagsTo(RuleMethod)
     */
    private static final Set<ParserAnnotation> FLAGS_SET = EnumSet.of(
        DONT_LABEL
    );

    private static final Map<String, ParserAnnotation>
        REVERSE_MAP;

    static {
        final ImmutableMap.Builder<String, ParserAnnotation> builder
            = ImmutableMap.builder();

        for (final ParserAnnotation entry: values())
            builder.put(entry.descriptor, entry);

        REVERSE_MAP = builder.build();
    }


    private final String descriptor;

    ParserAnnotation(final Class<? extends Annotation> c)
    {
        descriptor = Type.getType(c).getDescriptor();
    }

    /**
     * Record an enumeration value into a set if the descriptor is known
     *
     * @param set the set to record into
     * @param desc the descriptor
     * @return true if the descriptor is known
     */
    public static boolean recordAnnotation(final Set<ParserAnnotation> set,
        final String desc)
    {
        final ParserAnnotation annotation = REVERSE_MAP.get(desc);
        if (annotation == null)
            return false;
        set.add(annotation);
        return true;
    }

    /**
     * @see RuleMethod#moveFlagsTo(RuleMethod)
     *
     * @param from set to move flags from
     * @param  to set to move flags to
     */
    public static void moveTo(final Set<ParserAnnotation> from,
        final Set<ParserAnnotation> to)
    {
        final Set<ParserAnnotation> transferred = EnumSet.copyOf(from);
        transferred.retainAll(FLAGS_COPY);
        to.addAll(transferred);
        from.addAll(FLAGS_SET);
        from.removeAll(FLAGS_CLEAR);
    }
}
