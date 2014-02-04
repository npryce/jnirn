package com.natpryce.jnirn;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class ParsedClass {
    private final String name;
    public final File file;
    public final boolean isInstantiatedByNativeCode;
    public final List<ParsedMethod> nativeMethods;
    public final List<ParsedMethod> callbackMethods;

    public ParsedClass(String name, File file, boolean instantiatedByNativeCode, Multimap<String, Method> nativeMethods, Multimap<String, Method> callbackMethods) {
        this.name = name;
        this.file = file;
        this.isInstantiatedByNativeCode = instantiatedByNativeCode;
        this.nativeMethods = aslist(nativeMethods);
        this.callbackMethods = aslist(callbackMethods);
    }

    private List<ParsedMethod> aslist(final Multimap<String, Method> nativeMethods) {
        return newArrayList(
                Iterables.transform(nativeMethods.entries(), new Function<Map.Entry<String, Method>, ParsedMethod>() {
                    @Override
                    public ParsedMethod apply(Map.Entry<String, Method> input) {
                        return new ParsedMethod(
                                input.getKey(),
                                input.getValue(),
                                nativeMethods.get(input.getKey()).size() > 1
                        );
                    }
                })
        );
    }


    public static final Function<ParsedClass, File> toFile = new Function<ParsedClass, File>() {
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

    public String className() {
        return name;
    }

    public String cclass() {
        return name.replace("/", "_");
    }
}
