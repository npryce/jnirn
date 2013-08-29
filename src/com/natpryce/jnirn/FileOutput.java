package com.natpryce.jnirn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileOutput implements Output {
    private final String file;
    private final OutputFormat format;

    public FileOutput(String file, OutputFormat format) {
        this.format = format;
        this.file = file;
    }

    @Override
    public void write(Iterable<ParsedClass> classes) throws IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        try {
            format.writeTo(writer, classes);
        }
        finally {
            writer.close();
        }
    }
}
