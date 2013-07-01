package com.natpryce.jnirn;

public class NativeMethod {
    public final String name;
    public final String desc;
    public final String signature;
    public final String[] exceptions;

    public NativeMethod(String name, String desc, String signature, String[] exceptions) {
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }
}
