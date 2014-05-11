package com.github.parboiled1.grappa.transform;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.parboiled.transform.InstructionGroup;
import org.parboiled.transform.process.InstructionGroupPreparer;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Hashing for an {@link InstructionGroup}
 *
 * <p>Not very pretty, that one.</p>
 *
 * <p>The only entry point, {@link #hash(InstructionGroup, String)}, will hash
 * the entire instructions and field out of the instruction group, then set
 * the name of the instruction group appropriately.</p>
 *
 * @see InstructionGroupPreparer
 */
//TODO: use more than 16 chars; means updating all string-based bytecode tests
public final class InstructionGroupHasher
    extends MethodVisitor
{
    private static final BaseEncoding BASE_ENCODING
        = BaseEncoding.base32Hex();
    private static final HashFunction SHA1 = Hashing.sha1();

    private final String className;
    private final InstructionGroup group;

    private final Hasher hasher;
    private final LdcInstructionHashHelper ldcHelper;

    private final Set<Label> labels = Sets.newLinkedHashSet();
    private int nrLabels = 0;

    public static void hash(@Nonnull final InstructionGroup group,
        @Nonnull final String className)
    {
        final InstructionGroupHasher groupHasher
            = new InstructionGroupHasher(group, className);
        final String name = groupHasher.hashAndGetName();
        group.setName(name);
    }

    private InstructionGroupHasher(@Nonnull final InstructionGroup group,
        @Nonnull final String className)
    {
        super(Opcodes.ASM4);
        this.group = Preconditions.checkNotNull(group);
        this.className = Preconditions.checkNotNull(className);
        hasher = SHA1.newHasher();
        ldcHelper = new LdcInstructionHashHelper(hasher);
    }

    private String hashAndGetName()
    {
        group.getInstructions().accept(this);
        for (final FieldNode node: group.getFields())
            hasher.putUnencodedChars(Strings.nullToEmpty(node.name))
                .putUnencodedChars(Strings.nullToEmpty(node.desc))
                .putUnencodedChars(Strings.nullToEmpty(node.signature));
        final byte[] hash = new byte[10];
        hasher.hash().writeBytesTo(hash, 0, 10);
        final String prefix = group.getRoot().isActionRoot()
            ? "Action$" : "VarInit$";
        return prefix + BASE_ENCODING.encode(hash);
    }

    /**
     * Visits a zero operand instruction.
     *
     * @param opcode the opcode of the instruction to be visited. This opcode is
     * either NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1,
     * ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1,
     * FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD,
     * LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
     * IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
     * SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1,
     * DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
     * IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
     * FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
     * IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D,
     * L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
     * LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
     * DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER,
     * or MONITOREXIT.
     */
    @Override
    public void visitInsn(final int opcode)
    {
        hasher.putInt(opcode);
    }

    /**
     * Visits an instruction with a single int operand.
     *
     * @param opcode the opcode of the instruction to be visited. This opcode is
     * either BIPUSH, SIPUSH or NEWARRAY.
     * @param operand the operand of the instruction to be visited.<br>
     * When opcode is BIPUSH, operand value should be between
     * Byte.MIN_VALUE and Byte.MAX_VALUE.<br>
     * When opcode is SIPUSH, operand value should be between
     * Short.MIN_VALUE and Short.MAX_VALUE.<br>
     * When opcode is NEWARRAY, operand value should be one of
     * {@link Opcodes#T_BOOLEAN}, {@link Opcodes#T_CHAR},
     * {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE},
     * {@link Opcodes#T_BYTE}, {@link Opcodes#T_SHORT},
     * {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
     */
    @Override
    public void visitIntInsn(final int opcode, final int operand)
    {
        hasher.putInt(opcode).putInt(operand);
    }

    /**
     * Visits a local variable instruction. A local variable instruction is an
     * instruction that loads or stores the value of a local variable.
     *
     * @param opcode the opcode of the local variable instruction to be visited.
     * This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD,
     * ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var the operand of the instruction to be visited. This operand is
     */
    @Override
    public void visitVarInsn(final int opcode, final int var)
    {
        hasher.putInt(opcode).putInt(var);
        /*
         * (copied from original code) Make sure the names of identical actions
         * differ if thet are defined in different parent classes
         *
         * TODO: why var == 0? What does it mean?
         */
        if (opcode == Opcodes.ALOAD && var == 0)
            hasher.putUnencodedChars(className);
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that
     * takes the internal name of a class as parameter.
     *
     * @param opcode the opcode of the type instruction to be visited. This
     * opcode
     * is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type the operand of the instruction to be visited. This operand
     * must be the internal name of an object or array class (see
     * {@link Type#getInternalName() getInternalName}).
     */
    @Override
    public void visitTypeInsn(final int opcode, final String type)
    {
        hasher.putInt(opcode).putUnencodedChars(type);
    }

    /**
     * Visits a field instruction. A field instruction is an instruction that
     * loads or stores the value of a field of an object.
     *
     * @param opcode the opcode of the type instruction to be visited. This
     * opcode
     * is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner the internal name of the field's owner class (see
     * {@link Type#getInternalName() getInternalName}).
     * @param name the field's name.
     * @param desc the field's descriptor (see {@link Type Type}).
     */
    @Override
    public void visitFieldInsn(final int opcode, final String owner,
        final String name, final String desc)
    {
        hasher.putInt(opcode).putUnencodedChars(owner)
            .putUnencodedChars(name).putUnencodedChars(desc);
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     *
     * @param opcode the opcode of the type instruction to be visited. This
     * opcode
     * is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     * INVOKEINTERFACE.
     * @param owner the internal name of the method's owner class (see
     * {@link Type#getInternalName() getInternalName}).
     * @param name the method's name.
     * @param desc the method's descriptor (see {@link Type Type}).
     * @param itf
     */
    @Override
    public void visitMethodInsn(final int opcode, final String owner,
        final String name, final String desc, final boolean itf)
    {
        hasher.putInt(opcode).putUnencodedChars(owner)
            .putUnencodedChars(name).putUnencodedChars(desc);
    }

    /**
     * Visits a jump instruction. A jump instruction is an instruction that may
     * jump to another instruction.
     *
     * @param opcode the opcode of the type instruction to be visited. This
     * opcode
     * is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
     * IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
     * IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
     * @param label the operand of the instruction to be visited. This
     * operand is
     * a label that designates the instruction to which the jump
     */
    @Override
    public void visitJumpInsn(final int opcode, final Label label)
    {
        hasher.putInt(opcode);
        visitLabel(label);
    }

    /**
     * Visits a label. A label designates the instruction that will be visited
     * just after it.
     *
     * @param label a {@link Label Label} object.
     */
    @Override
    public void visitLabel(final Label label)
    {
        if (!labels.add(label))
            hasher.putInt(nrLabels++);
    }

    /**
     * Visits a LDC instruction. Note that new constant types may be added in
     * future versions of the Java Virtual Machine. To easily detect new
     * constant types, implementations of this method should check for
     * unexpected constant types, like this:
     * <pre>
     * if (cst instanceof Integer) {
     *     // ...
     * } else if (cst instanceof Float) {
     *     // ...
     * } else if (cst instanceof Long) {
     *     // ...
     * } else if (cst instanceof Double) {
     *     // ...
     * } else if (cst instanceof String) {
     *     // ...
     * } else if (cst instanceof Type) {
     *     int sort = ((Type) cst).getSort();
     *     if (sort == Type.OBJECT) {
     *         // ...
     *     } else if (sort == Type.ARRAY) {
     *         // ...
     *     } else if (sort == Type.METHOD) {
     *         // ...
     *     } else {
     *         // throw an exception
     *     }
     * } else if (cst instanceof Handle) {
     *     // ...
     * } else {
     *     // throw an exception
     * }
     * </pre>
     *
     * @param cst the constant to be loaded on the stack. This parameter must be
     * a non null {@link Integer}, a {@link Float}, a {@link Long}, a
     * {@link Double}, a {@link String}, a {@link Type} of OBJECT or
     * ARRAY sort for <tt>.class</tt> constants, for classes whose
     * version is 49.0, a {@link Type} of METHOD sort or a
     * {@link Handle} for MethodType and MethodHandle constants, for
     * classes whose version is 51.0.
     */
    @Override
    public void visitLdcInsn(final Object cst)
    {
        ldcHelper.hashLdc(cst);
    }

    /**
     * Visits an IINC instruction.
     *
     * @param var index of the local variable to be incremented.
     * @param increment
     */
    @Override
    public void visitIincInsn(final int var, final int increment)
    {
        hasher.putInt(var).putInt(increment);
    }

    /**
     * Visits a TABLESWITCH instruction.
     *
     * @param min the minimum key value.
     * @param max the maximum key value.
     * @param dflt beginning of the default handler block.
     * @param labels beginnings of the handler blocks. <tt>labels[i]</tt> is the
     */
    @Override
    public void visitTableSwitchInsn(final int min, final int max,
        final Label dflt, final Label... labels)
    {
        hasher.putInt(min).putInt(max);
        visitLabel(dflt);
        for (final Label label: labels)
            visitLabel(label);
    }

    /**
     * Visits a LOOKUPSWITCH instruction.
     *
     * @param dflt beginning of the default handler block.
     * @param keys the values of the keys.
     * @param labels beginnings of the handler blocks. <tt>labels[i]</tt> is the
     */
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
        final Label[] labels)
    {
        visitLabel(dflt);
        for (int i = 0; i < keys.length; i++) {
            hasher.putInt(keys[i]);
            visitLabel(labels[i]);
        }
    }

    /**
     * Visits a MULTIANEWARRAY instruction.
     *
     * @param desc an array type descriptor (see {@link Type Type}).
     * @param dims
     */
    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims)
    {
        hasher.putUnencodedChars(desc).putInt(dims);
    }

    /**
     * Visits a try catch block.
     *
     * @param start beginning of the exception handler's scope (inclusive).
     * @param end end of the exception handler's scope (exclusive).
     * @param handler beginning of the exception handler's code.
     * @param type internal name of the type of exceptions handled by the
     * handler, or <tt>null</tt> to catch any exceptions (for
     * "finally" blocks).
     * @throws IllegalArgumentException if one of the labels has already been
     * visited by this visitor
     * (by the {@link #visitLabel visitLabel} method).
     */
    @Override
    public void visitTryCatchBlock(final Label start, final Label end,
        final Label handler, final String type)
    {
        visitLabel(start);
        visitLabel(end);
        visitLabel(handler);
        hasher.putUnencodedChars(type);
    }
}
