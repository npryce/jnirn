package com.natpryce.jnirn;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MakeDependencyOutput {
    private final String outputCSourceFile;
    private final List<File> inputFiles;

    public MakeDependencyOutput(String outputCSourceFile, List<File> inputFiles) {
        this.outputCSourceFile = outputCSourceFile;
        this.inputFiles = inputFiles;
    }

    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) throws IOException {
        for (File f : Sets.newTreeSet(Iterables.transform(classes, ParsedClass.toFile))) {
            writer.print(outputCSourceFile);
            writer.print(": ");
            writer.println(f);
        }
    }
}
