package com.natpryce.jnirn;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.*;

public class NativeMethodCollector extends ClassVisitor {
    public final Map<String, List<NativeMethod>> nativeMethodsByName = new LinkedHashMap<String, List<NativeMethod>>();

    public NativeMethodCollector() {
        super(Opcodes.ASM4);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            List<NativeMethod> overloads = nativeMethodsByName.get(name);
            if (overloads == null) {
                overloads = new ArrayList<NativeMethod>();
                nativeMethodsByName.put(name, overloads);
            }

            overloads.add(new NativeMethod(name, Type.getMethodType(desc), signature, exceptions));
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    boolean foundNativeMethods() {
        return !nativeMethodsByName.isEmpty();
    }
}
