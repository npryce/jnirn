package com.natpryce.jnirn;

import com.google.common.collect.Multimap;

import java.io.File;

public class ParsedClass {
    public final String name;
    public final File file;
    public Multimap<String, NativeMethod> nativeMethods;

    public ParsedClass(String name, File file, Multimap<String, NativeMethod> nativeMethods) {
        this.name = name;
        this.file = file;
        this.nativeMethods = nativeMethods;
    }

}
