package com.natpryce.jnirn;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JNIRN {
    private final SortedMap<String, Map<String,List<NativeMethod>>> nativeMethodsByClass = new TreeMap<String, Map<String, List<NativeMethod>>>();

    public static void main(String... args) throws IOException {
        JNIRN app = new JNIRN();
        app.run(args);
    }

    public void run(String... args) throws IOException {
        JavaBytecodeParser parser = new JavaBytecodeParser();
        for (String arg : args) {
            parser.parse(new File(arg));
        }

        CSourceOutput cSourceOutput = new CSourceOutput();
        final PrintWriter writer = new PrintWriter(System.out);
        try {
            cSourceOutput.writeTo(writer, parser.nativeMethodsByClass());
        } finally {
            writer.flush();
        }
    }
}
