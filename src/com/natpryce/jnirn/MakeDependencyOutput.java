package com.natpryce.jnirn;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class MakeDependencyOutput {
    private final String outputCSourceFile;

    public MakeDependencyOutput(String outputCSourceFile) {
        this.outputCSourceFile = outputCSourceFile;
    }

    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) throws IOException {
        for (File f : Sets.newTreeSet(Iterables.transform(classes, ParsedClass.toFile))) {
            writer.print(outputCSourceFile);
            writer.print(": ");
            writer.println(f);
        }
    }
}
