package com.natpryce.jnirn.output;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.natpryce.jnirn.OutputFormat;
import com.natpryce.jnirn.ParsedClass;

import java.io.File;
import java.io.PrintWriter;

public class MakeDependencyFormat implements OutputFormat {
    private final String outputCSourceFile;

    public MakeDependencyFormat(String outputCSourceFile) {
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
