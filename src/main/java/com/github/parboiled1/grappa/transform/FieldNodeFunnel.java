package com.github.parboiled1.grappa.transform;

import com.google.common.base.Strings;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.objectweb.asm.tree.FieldNode;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link Funnel} for an ASM {@link FieldNode}
 */
@ParametersAreNonnullByDefault
public enum FieldNodeFunnel
    implements Funnel<FieldNode>
{
    INSTANCE
    {
        /**
         * Sends a stream of data from the {@code from} object into the
         * sink {@code into}. There
         * is no requirement that this data be complete enough to fully
         * reconstitute the object
         * later.
         *
         * @param from
         * @param into
         * @since 12.0 (in Guava 11.0, {@code PrimitiveSink} was named
         * {@code Sink})
         */
        @Override
        public void funnel(final FieldNode from, final PrimitiveSink into)
        {
            into.putUnencodedChars(Strings.nullToEmpty(from.name))
                .putUnencodedChars(Strings.nullToEmpty(from.desc))
                .putUnencodedChars(Strings.nullToEmpty(from.signature));
        }
    }
}
