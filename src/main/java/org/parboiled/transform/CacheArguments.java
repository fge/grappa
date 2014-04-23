package org.parboiled.transform;

import org.objectweb.asm.Type;
import org.parboiled.annotations.Cached;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.parboiled.common.Utils.toObjectArray;

/**
 * Runtime cache arguments for generated parsers
 *
 * <p>This class is used by generated parsers for rules taking arguments and
 * annotated with {@link Cached}. In this case, a {@link Map} is generated whose
 * keys are instances of this class, and values are already generated rules.</p>
 *
 * <p>What this class basically does is generate a "digest" of rule
 * arguments (as an {@code Object[]}.</p>
 */
public final class CacheArguments
{
    private final Object[] params;

    public CacheArguments(final Object... params)
    {
        // we need to "unroll" all inner Object arrays
        final List<Object> list = new ArrayList<Object>(params.length);
        unroll(params, list);
        this.params = list.toArray();
    }

    private static void unroll(final Object[] params, final List<Object> list)
    {
        Class<?> c;
        int type;
        for (final Object param: params) {
            if (param == null)
                continue;
            c = param.getClass();
            if (!c.isArray()) {
                list.add(param);
                continue;
            }
            type = Type.getType(c.getComponentType()).getSort();
            switch (type)
            {
                case Type.BOOLEAN:
                    unroll(toObjectArray((boolean[]) param), list);
                    continue;
                case Type.BYTE:
                    unroll(toObjectArray((byte[]) param), list);
                    continue;
                case Type.CHAR:
                    unroll(toObjectArray((char[]) param), list);
                    continue;
                case Type.DOUBLE:
                    unroll(toObjectArray((double[]) param), list);
                    continue;
                case Type.FLOAT:
                    unroll(toObjectArray((float[]) param), list);
                    continue;
                case Type.INT:
                    unroll(toObjectArray((int[]) param), list);
                    continue;
                case Type.LONG:
                    unroll(toObjectArray((long[]) param), list);
                    continue;
                case Type.SHORT:
                    unroll(toObjectArray((short[]) param), list);
                    continue;
                case Type.OBJECT:
                case Type.ARRAY:
                    unroll((Object[]) param, list);
                    continue;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override
    public boolean equals(final Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (getClass() != o.getClass())
            return false;
        final CacheArguments other = (CacheArguments) o;
        return Arrays.equals(params, other.params);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(params);
    }
}
