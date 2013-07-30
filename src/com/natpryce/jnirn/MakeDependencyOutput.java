package com.natpryce.jnirn;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MakeDependencyOutput {
    private final List<File> inputFiles;

    public MakeDependencyOutput(String outputCSourceFile, List<File> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public void writeTo(PrintWriter writer, Iterable<ParsedClass> nativeMethodsByClass) throws IOException {
        throw new IOException("not implemented yet");
    }
}
