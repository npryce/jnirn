package com.natpryce.jnirn;

import com.google.common.base.Function;
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

    public static final Function<ParsedClass,File> toFile  = new Function<ParsedClass, File>() {
        @Override
        public File apply(ParsedClass input) {
            return input.file;
        }
    };
}
