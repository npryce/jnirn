package com.natpryce.jnirn;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.PrintWriter;

public class MakeDependencyOutput implements OutputFormat {
    private final String outputCSourceFile;

    public MakeDependencyOutput(String outputCSourceFile) {
        this.outputCSourceFile = outputCSourceFile;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        for (File f : Sets.newTreeSet(Iterables.transform(classes, ParsedClass.toFile))) {
            writer.print(outputCSourceFile);
            writer.print(": ");
            writer.println(f);
        }
    }
}
