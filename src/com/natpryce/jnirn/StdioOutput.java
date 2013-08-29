package com.natpryce.jnirn;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class StdioOutput implements Output {
    private final PrintStream out;
    private final OutputFormat outputFormat;

    public StdioOutput(PrintStream out, OutputFormat outputFormat) {
        this.out = out;
        this.outputFormat = outputFormat;
    }

    @Override
    public void write(Iterable<ParsedClass> classes) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        outputFormat.writeTo(writer, classes);
        writer.flush();
    }
}
