package com.natpryce.jnirn;

import com.natpryce.jnirn.proguard.ClassMethod;
import com.natpryce.jnirn.proguard.Obfuscation;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ParsedMethod {
    public final Obfuscatable<Method> method;

    public final boolean overloaded;
    public final boolean isNative;
    public final boolean isCalledBack;

    public ParsedMethod(Method method, boolean overloaded, boolean isNative, boolean isCalledBack) {
        this.method = Obfuscatable.of(method);
        this.isNative = isNative;
        this.isCalledBack = isCalledBack;
        this.overloaded = overloaded;
    }

    private ParsedMethod(Obfuscatable<Method> method, boolean overloaded, boolean isNative, boolean isCalledBack) {
        this.method = method;
        this.isNative = isNative;
        this.isCalledBack = isCalledBack;
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

    public ParsedMethod obfuscate(String className, Obfuscation obfuscation) {
        return new ParsedMethod(
                method.obfuscatedAs(
                        // hack to not change this method mapper just now...
                        obfuscation.mapMethod(new ClassMethod(className, method.inSource)).method),
                overloaded, isNative, isCalledBack
        );
    }
}
