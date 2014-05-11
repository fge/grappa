package com.github.parboiled1.grappa.transform;

import com.google.common.collect.Sets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.objectweb.asm.Label;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

/**
 * A {@link Funnel} for an (ASM) {@link Label}
 *
 * @see InstructionGroupHasher#visitLabel(Label)
 */
@ParametersAreNonnullByDefault
public final class LabelFunnel
    implements Funnel<Label>
{
    private final Set<Label> labels = Sets.newHashSet();
    private int index = 0;

    /**
     * Sends a stream of data from the {@code from} object into the sink
     * {@code into}. There
     * is no requirement that this data be complete enough to fully
     * reconstitute the object
     * later.
     *
     * @param from
     * @param into
     * @since 12.0 (in Guava 11.0, {@code PrimitiveSink} was named {@code Sink})
     */
    @Override
    public void funnel(final Label from, final PrimitiveSink into)
    {
        if (labels.add(from))
            into.putInt(index++);
    }
}
