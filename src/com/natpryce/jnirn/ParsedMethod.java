package com.natpryce.jnirn;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ParsedMethod {
    public final Obfuscatable<Method> method;

    public final boolean overloaded;
    public final boolean isNative;
    public final boolean isCalledBack;

    public ParsedMethod(Method method, boolean overloaded, boolean isNative, boolean isCalledBack) {
        this.isNative = isNative;
        this.isCalledBack = isCalledBack;
        this.method = Obfuscatable.of(method);
        this.overloaded = overloaded;
    }

    public String descriptor() {
        return method.atRuntime.getDescriptor();
    }

    public String cmethod() {
        Method sourceMethod = method.inSource;
        String v = sourceMethod.getName().replace("_", "_1");
        return !overloaded ? v : v + "__" + signatureToC(sourceMethod);
    }

    private String signatureToC(Method sourceMethod) {
        StringBuilder b = new StringBuilder();

        for (Type argumentType : sourceMethod.getArgumentTypes()) {
            b.append(argumentType.getDescriptor().replace("/", "_").replace(";", "_2"));
        }

        return b.toString();
    }
}
