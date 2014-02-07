package com.natpryce.jnirn.proguard;

import org.objectweb.asm.commons.Method;

public class ClassMethod {
    public final String className;
    public final Method method;

    public ClassMethod(String className, Method method) {
        this.className = className;
        this.method = method;
    }
}
