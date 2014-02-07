package com.natpryce.jnirn;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.natpryce.jnirn.proguard.Obfuscation;

import java.io.IOException;

public class Codebase {

    private final Iterable<ParsedClass> classes;

    public Codebase(Iterable<ParsedClass> classes) {
        this.classes = classes;
    }

    public void writeTo(Output output) throws IOException {
        output.write(classes);
    }

    public Codebase obfuscate(final Obfuscation mapper) {
        return new Codebase(Iterables.transform(classes, new Function<ParsedClass, ParsedClass>() {
            @Override
            public ParsedClass apply(ParsedClass parsedClass) {
                return parsedClass.obfuscate(mapper);
            }
        }));
    }
}
