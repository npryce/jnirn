package com.natpryce.jnirn;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ParsedClass {
    private final Name _name;
    public final File file;
    public final boolean isInstantiatedByNativeCode;
    public final List<ParsedMethod> nativeMethods;
    public final List<ParsedMethod> callbackMethods;

    public ParsedClass(String name, File file, boolean instantiatedByNativeCode, Multimap<String, Method> nativeMethods, Multimap<String, Method> callbackMethods) {
        this._name = new Name(name);
        this.file = file;
        this.isInstantiatedByNativeCode = instantiatedByNativeCode;
        this.nativeMethods = aslist(nativeMethods);
        this.callbackMethods = aslist(callbackMethods);
    }

    private List<ParsedMethod> aslist(final Multimap<String, Method> nativeMethods) {
        return newArrayList(
                Iterables.transform(nativeMethods.values(), new Function<Method, ParsedMethod>() {
                    @Override
                    public ParsedMethod apply(Method method) {
                        boolean isOverloaded = nativeMethods.get(method.getName()).size() > 1;

                        return new ParsedMethod(ParsedClass.this, method, isOverloaded);
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
        return _name.atRuntime;
    }

    public String cclass() {
        return _name.inSource.replace("/", "_");
    }
}
