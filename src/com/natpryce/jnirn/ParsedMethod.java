package com.natpryce.jnirn;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ParsedMethod {
    public final Method method;
    public final boolean overloaded;

    public ParsedMethod(Method method, boolean overloaded) {
        this.method = method;
        this.overloaded = overloaded;
    }

    public String methodName() {
        return method.getName();
    }

    public String descriptor() {
        return method.getDescriptor();
    }

    private String signatureToC() {
        StringBuilder b = new StringBuilder();

        for (Type argumentType : method.getArgumentTypes()) {
            b.append(argumentType.getDescriptor().replace("/", "_").replace(";", "_2"));
        }

        return b.toString();
    }

    public String cmethod() {
        String v = methodName().replace("_", "_1");
        if (!overloaded) {
            return v;
        }
        return v + "__" + signatureToC();
    }
}
