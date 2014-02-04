package com.natpryce.jnirn;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ParsedMethod {
    public final String name;
    public final Method method;
    public final boolean overloaded;

    public ParsedMethod(String name, Method method, boolean overloaded) {
        this.name = name;
        this.method = method;
        this.overloaded = overloaded;
    }

    public String methodName() {
        return this.method.getName();
    }

    public String descriptor() {
        return this.method.getDescriptor();
    }

    private String signatureToC() {
        StringBuilder b = new StringBuilder();

        for (Type argumentType : this.method.getArgumentTypes()) {
            b.append(argumentType.getDescriptor().replace("/", "_").replace(";", "_2"));
        }

        return b.toString();
    }

    public String cmethod() {
        String v = name.replace("_", "_1");
        if (!overloaded) {
            return v;
        }
        return v + "__" + signatureToC();
    }
}
