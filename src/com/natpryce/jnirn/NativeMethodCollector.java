package com.natpryce.jnirn;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class NativeMethodCollector extends ClassVisitor {
    public final Multimap<String, NativeMethod> nativeMethodsByName = LinkedHashMultimap.create();

    public NativeMethodCollector() {
        super(Opcodes.ASM4);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            nativeMethodsByName.put(name, new NativeMethod(name, Type.getMethodType(desc), signature, exceptions));
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    boolean foundNativeMethods() {
        return !nativeMethodsByName.isEmpty();
    }
}
