package com.natpryce.jnirn;

import com.google.common.base.Function;

public class Name {
    public final String sourceName;
    public final String binaryName;

    public Name(String name) {
        this(name, name);
    }

    public Name(String sourceName, String binaryName) {
        this.sourceName = sourceName;
        this.binaryName = binaryName;
    }

    public Name obfuscated(Function<String,String> obfuscation) {
        return new Name(sourceName, obfuscation.apply(binaryName));
    }
}
