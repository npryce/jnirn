package com.natpryce.jnirn;

public class Obfuscatable<T> {
    public final T inSource;
    public final T atRuntime;

    private Obfuscatable(T name) {
        this(name, name);
    }

    private Obfuscatable(T sourceName, T runtimeName) {
        this.inSource = sourceName;
        this.atRuntime = runtimeName;
    }

    public static <T> Obfuscatable<T> of(T unobfuscated) {
        return new Obfuscatable<T>(unobfuscated);
    }
}
