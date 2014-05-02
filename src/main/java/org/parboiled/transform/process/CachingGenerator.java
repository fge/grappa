/*
 * Copyright (c) 2009-2010 Ken Wenzel and Mathias Doenitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.parboiled.transform.process;

import com.google.common.base.Preconditions;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.ProxyMatcher;
import org.parboiled.transform.CacheArguments;
import org.parboiled.transform.ParserClassNode;
import org.parboiled.transform.RuleMethod;
import org.parboiled.transform.Types;
import org.parboiled.transform.asm.AsmHelper;
import org.parboiled.transform.asm.ClassHelper;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.DUP_X2;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.SWAP;

/**
 * Wraps the method code with caching and proxying constructs.
 */
public class CachingGenerator implements RuleMethodProcessor
{

    private ParserClassNode classNode;
    private RuleMethod method;
    private InsnList instructions;
    private AbstractInsnNode current;
    private String cacheFieldName;

    @Override
    public boolean appliesTo(final ParserClassNode classNode, final RuleMethod method) {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        return method.hasCachedAnnotation();
    }

    @Override
    public void process(final ParserClassNode classNode, final RuleMethod method) throws Exception {
        Preconditions.checkNotNull(classNode, "classNode");
        Preconditions.checkNotNull(method, "method");
        Preconditions.checkState(!method.isSuperMethod()); // super methods
        // have flag
        // moved to the overriding method

        this.classNode = classNode;
        this.method = method;
        this.instructions = method.instructions;
        this.current = instructions.getFirst();

        generateCacheHitReturn();
        generateStoreNewProxyMatcher();
        seekToReturnInstruction();
        generateArmProxyMatcher();
        generateStoreInCache();
    }

    // if (<cache> != null) return <cache>;
    private void generateCacheHitReturn() {
        // stack:
        generateGetFromCache();
        // stack: <cachedValue>
        insert(new InsnNode(DUP));
        // stack: <cachedValue> :: <cachedValue>
        final LabelNode cacheMissLabel = new LabelNode();
        insert(new JumpInsnNode(IFNULL, cacheMissLabel));
        // stack: <cachedValue>
        insert(new InsnNode(ARETURN));
        // stack: <null>
        insert(cacheMissLabel);
        // stack: <null>
        insert(new InsnNode(POP));
        // stack:
    }

    @SuppressWarnings("unchecked")
    private void generateGetFromCache() {
        final Type[] paramTypes = Type.getArgumentTypes(method.desc);
        cacheFieldName = findUnusedCacheFieldName();

        // if we have no parameters we use a simple Rule field as cache, otherwise a HashMap
        final String cacheFieldDesc = paramTypes.length == 0 ? Types.RULE_DESC : "Ljava/util/HashMap;";
        classNode.fields.add(new FieldNode(ACC_PRIVATE, cacheFieldName, cacheFieldDesc, null, null));

        // stack:
        insert(new VarInsnNode(ALOAD, 0));
        // stack: <this>
        insert(new FieldInsnNode(GETFIELD, classNode.name, cacheFieldName, cacheFieldDesc));
        // stack: <cache>

        if (paramTypes.length == 0) return; // if we have no parameters we are done

        // generate: if (<cache> == null) <cache> = new HashMap<Object, Rule>();

        // stack: <hashMap>
        insert(new InsnNode(DUP));
        // stack: <hashMap> :: <hashMap>
        final LabelNode alreadyInitialized = new LabelNode();
        insert(new JumpInsnNode(IFNONNULL, alreadyInitialized));
        // stack: <null>
        insert(new InsnNode(POP));
        // stack:
        insert(new VarInsnNode(ALOAD, 0));
        // stack: <this>
        insert(new TypeInsnNode(NEW, "java/util/HashMap"));
        // stack: <this> :: <hashMap>
        insert(new InsnNode(DUP_X1));
        // stack: <hashMap> :: <this> :: <hashMap>
        insert(new InsnNode(DUP));
        // stack: <hashMap> :: <this> :: <hashMap> :: <hashMap>
        insert(new MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>",
            "()V", false));
        // stack: <hashMap> :: <this> :: <hashMap>
        insert(new FieldInsnNode(PUTFIELD, classNode.name, cacheFieldName, cacheFieldDesc));
        // stack: <hashMap>
        insert(alreadyInitialized);
        // stack: <hashMap>

        // if we have more than one parameter or the parameter is an array we have to wrap with our Arguments class
        // since we need to unroll all inner arrays and apply custom hashCode(...) and equals(...) implementations
        if (paramTypes.length > 1 || paramTypes[0].getSort() == Type.ARRAY) {
            // generate: push new Arguments(new Object[] {<params>})

            final String arguments = Type.getInternalName(CacheArguments.class);
            // stack: <hashMap>
            insert(new TypeInsnNode(NEW, arguments));
            // stack: <hashMap> :: <arguments>
            insert(new InsnNode(DUP));
            // stack: <hashMap> :: <arguments> :: <arguments>
            generatePushNewParameterObjectArray(paramTypes);
            // stack: <hashMap> :: <arguments> :: <arguments> :: <array>
            insert(new MethodInsnNode(INVOKESPECIAL, arguments, "<init>",
                "([Ljava/lang/Object;)V", false));
            // stack: <hashMap> :: <arguments>
        } else {
            // stack: <hashMap>
            generatePushParameterAsObject(paramTypes, 0);
            // stack: <hashMap> :: <param>
        }

        // generate: <hashMap>.get(...)

        // stack: <hashMap> :: <mapKey>
        insert(new InsnNode(DUP));
        // stack: <hashMap> :: <mapKey> :: <mapKey>
        insert(new VarInsnNode(ASTORE, method.maxLocals));
        // stack: <hashMap> :: <mapKey>
        insert(new MethodInsnNode(INVOKEVIRTUAL, "java/util/HashMap", "get",
            "(Ljava/lang/Object;)Ljava/lang/Object;", false));
        // stack: <object>
        insert(new TypeInsnNode(CHECKCAST, Types.RULE.getInternalName()));
        // stack: <rule>
    }

    @SuppressWarnings("unchecked")
    private String findUnusedCacheFieldName() {
        String name = "cache$" + method.name;
        int i = 2;
        while (hasField(name)) {
            name = "cache$" + method.name + i++;
        }
        return name;
    }

    public boolean hasField(final String fieldName) {
        for (final Object field : classNode.fields) {
            if (fieldName.equals(((FieldNode) field).name)) return true;
        }
        return false;
    }

    private void generatePushNewParameterObjectArray(final Type[] paramTypes) {
        // stack: ...
        insert(new IntInsnNode(BIPUSH, paramTypes.length));
        // stack: ... :: <length>
        insert(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
        // stack: ... :: <array>

        for (int i = 0; i < paramTypes.length; i++) {
            // stack: ... :: <array>
            insert(new InsnNode(DUP));
            // stack: ... :: <array> :: <array>
            insert(new IntInsnNode(BIPUSH, i));
            // stack: ... :: <array> :: <array> :: <index>
            generatePushParameterAsObject(paramTypes, i);
            // stack: ... :: <array> :: <array> :: <index> :: <param>
            insert(new InsnNode(AASTORE));
            // stack: ... :: <array>
        }
        // stack: ... :: <array>
    }

    private void generatePushParameterAsObject(final Type[] paramTypes, int parameterNr) {
        switch (paramTypes[parameterNr++].getSort()) {
            case Type.BOOLEAN:
                insert(new VarInsnNode(ILOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean",
                    "valueOf", "(Z)Ljava/lang/Boolean;", false));
                return;
            case Type.CHAR:
                insert(new VarInsnNode(ILOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Character",
                    "valueOf", "(C)Ljava/lang/Character;", false));
                return;
            case Type.BYTE:
                insert(new VarInsnNode(ILOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Byte",
                    "valueOf", "(B)Ljava/lang/Byte;", false));
                return;
            case Type.SHORT:
                insert(new VarInsnNode(ILOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Short",
                    "valueOf", "(S)Ljava/lang/Short;", false));
                return;
            case Type.INT:
                insert(new VarInsnNode(ILOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer",
                    "valueOf", "(I)Ljava/lang/Integer;", false));
                return;
            case Type.FLOAT:
                insert(new VarInsnNode(FLOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Float",
                    "valueOf", "(F)Ljava/lang/Float;", false));
                return;
            case Type.LONG:
                insert(new VarInsnNode(LLOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Long",
                    "valueOf", "(J)Ljava/lang/Long;", false));
                return;
            case Type.DOUBLE:
                insert(new VarInsnNode(DLOAD, parameterNr));
                insert(new MethodInsnNode(INVOKESTATIC, "java/lang/Double",
                    "valueOf", "(D)Ljava/lang/Double;", false));
                return;
            case Type.ARRAY:
            case Type.OBJECT:
                insert(new VarInsnNode(ALOAD, parameterNr));
                return;
            case Type.VOID:
            default:
                throw new IllegalStateException();
        }
    }

    // <cache> = new ProxyMatcher();
    private void generateStoreNewProxyMatcher() {
        final String proxyMatcherType = Types.PROXY_MATCHER.getInternalName();

        // stack:
        insert(new TypeInsnNode(NEW, proxyMatcherType));
        // stack: <proxyMatcher>
        insert(new InsnNode(DUP));
        // stack: <proxyMatcher> :: <proxyMatcher>
        insert(new MethodInsnNode(INVOKESPECIAL, proxyMatcherType, "<init>",
            "()V", false));
        // stack: <proxyMatcher>
        generateStoreInCache();
        // stack: <proxyMatcher>
    }

    private void seekToReturnInstruction() {
        while (current.getOpcode() != ARETURN) {
            current = current.getNext();
        }
    }

    // <proxyMatcher>.arm(<rule>)
    private void generateArmProxyMatcher() {
        final String proxyMatcherType = Types.PROXY_MATCHER.getInternalName();
        final ClassHelper helper = AsmHelper.classHelper(ProxyMatcher.class);

        // stack: <proxyMatcher> :: <rule>
        insert(new InsnNode(DUP_X1));
        // stack: <rule> :: <proxyMatcher> :: <rule>
        insert(new TypeInsnNode(CHECKCAST, Types.MATCHER.getInternalName()));
        // stack: <rule> :: <proxyMatcher> :: <matcher>
        insert(helper.voidMethodCall("arm", Matcher.class));
//        insert(new MethodInsnNode(INVOKEVIRTUAL, proxyMatcherType, "arm",
//            '(' + Types.MATCHER_DESC + ")V", false));
        // stack: <rule>
    }

    private void generateStoreInCache() {
        final Type[] paramTypes = Type.getArgumentTypes(method.desc);

        // stack: <rule>
        insert(new InsnNode(DUP));
        // stack: <rule> :: <rule>

        if (paramTypes.length == 0) {
            // stack: <rule> :: <rule>
            insert(new VarInsnNode(ALOAD, 0));
            // stack: <rule> :: <rule> :: <this>
            insert(new InsnNode(SWAP));
            // stack: <rule> :: <this> :: <rule>
            insert(new FieldInsnNode(PUTFIELD, classNode.name, cacheFieldName, Types.RULE_DESC));
            // stack: <rule>
            return;
        }

        // stack: <rule> :: <rule>
        insert(new VarInsnNode(ALOAD, method.maxLocals));
        // stack: <rule> :: <rule> :: <mapKey>
        insert(new InsnNode(SWAP));
        // stack: <rule> :: <mapKey> :: <rule>
        insert(new VarInsnNode(ALOAD, 0));
        // stack: <rule> :: <mapKey> :: <rule> :: <this>
        insert(new FieldInsnNode(GETFIELD, classNode.name, cacheFieldName, "Ljava/util/HashMap;"));
        // stack: <rule> :: <mapKey> :: <rule> :: <hashMap>
        insert(new InsnNode(DUP_X2));
        // stack: <rule> :: <hashMap> :: <mapKey> :: <rule> :: <hashMap>
        insert(new InsnNode(POP));
        // stack: <rule> :: <hashMap> :: <mapKey> :: <rule>
        insert(new MethodInsnNode(INVOKEVIRTUAL, "java/util/HashMap", "put",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
        // stack: <rule> :: <null>
        insert(new InsnNode(POP));
        // stack: <rule>
    }

    private void insert(final AbstractInsnNode instruction) {
        instructions.insertBefore(current, instruction);
    }
}
