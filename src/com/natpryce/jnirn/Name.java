package com.natpryce.jnirn;

import com.google.common.base.Function;

public class Name {
    public final String inSource;
    public final String atRuntime;

    public Name(String name) {
        this(name, name);
    }

    public Name(String sourceName, String runtimeName) {
        this.inSource = sourceName;
        this.atRuntime = runtimeName;
    }

    public Name obfuscated(Function<String,String> obfuscation) {
        return new Name(inSource, obfuscation.apply(atRuntime));
    }
}
