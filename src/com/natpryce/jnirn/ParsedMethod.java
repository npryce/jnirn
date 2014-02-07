package com.natpryce.jnirn;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ParsedMethod {
    public final ParsedClass owner;
    public final Obfuscatable<String> methodName;
    public final Method method;
    public final boolean overloaded;

    public ParsedMethod(ParsedClass owner, Method method, boolean overloaded) {
        this.owner = owner;
        this.methodName = Obfuscatable.of(method.getName());
        this.method = method;
        this.overloaded = overloaded;
    }

    public String descriptor() {
        return method.getDescriptor();
    }

    public String cmethod() {
        String v = methodName.inSource.replace("_", "_1");
        return !overloaded ? v : v + "__" + signatureToC();
    }

    private String signatureToC() {
        StringBuilder b = new StringBuilder();

        for (Type argumentType : method.getArgumentTypes()) {
            b.append(argumentType.getDescriptor().replace("/", "_").replace(";", "_2"));
        }

        return b.toString();
    }
}
