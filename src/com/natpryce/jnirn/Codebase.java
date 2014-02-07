package com.natpryce.jnirn;

import java.io.IOException;

public class Codebase {

    private final Iterable<ParsedClass> classes;

    public Codebase(Iterable<ParsedClass> classes) {
        this.classes = classes;
    }

    public void writeTo(Output output) throws IOException {
        output.write(classes);
    }
}
