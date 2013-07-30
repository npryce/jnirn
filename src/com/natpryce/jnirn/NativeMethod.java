package com.natpryce.jnirn;

import org.objectweb.asm.Type;

public class NativeMethod {
    public final String name;
    public final Type type;
    public final String signature;
    public final String[] exceptions;

    public NativeMethod(String name, Type type, String signature, String[] exceptions) {
        this.name = name;
        this.type = type;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    public Type[] argumentTypes() {
        return type.getArgumentTypes();
    }
}
