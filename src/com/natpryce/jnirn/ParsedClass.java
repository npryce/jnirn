package com.natpryce.jnirn;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.objectweb.asm.commons.Method;

import java.io.File;

public class ParsedClass {
    public final String name;
    public final File file;
    public final boolean isInstantiatedByNativeCode;
    public final ImmutableMultimap<String, Method> nativeMethods;
    public final ImmutableMultimap<String, Method> callbackMethods;

    public ParsedClass(String name, File file, boolean instantiatedByNativeCode, Multimap<String, Method> nativeMethods, Multimap<String, Method> callbackMethods) {
        this.name = name;
        this.file = file;
        this.isInstantiatedByNativeCode = instantiatedByNativeCode;
        this.nativeMethods = ImmutableMultimap.copyOf(nativeMethods);
        this.callbackMethods = ImmutableMultimap.copyOf(callbackMethods);
    }

    public static final Function<ParsedClass,File> toFile  = new Function<ParsedClass, File>() {
        @Override
        public File apply(ParsedClass input) {
            return input.file;
        }
    };

    public static Predicate<? super ParsedClass> hasNativeMethods = new Predicate<ParsedClass>() {
        @Override
        public boolean apply(ParsedClass c) {
            return !c.nativeMethods.isEmpty();
        }
    };

    public static Predicate<? super ParsedClass> hasCallbackMethods = new Predicate<ParsedClass>() {
        @Override
        public boolean apply(ParsedClass c) {
            return !c.callbackMethods.isEmpty();
        }
    };
}
