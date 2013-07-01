package com.natpryce.jnirn;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class NativeMethodCollector extends ClassVisitor {
    public final List<NativeMethod> nativeMethods = new ArrayList<NativeMethod>();

    public NativeMethodCollector() {
        super(Opcodes.ASM4);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            nativeMethods.add(new NativeMethod(name, desc, signature, exceptions));
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    boolean foundNativeMethods() {
        return !nativeMethods.isEmpty();
    }
}
