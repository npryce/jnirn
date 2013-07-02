package com.natpryce.jnirn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JNIRN {
    public static void main(String... args) throws IOException {
        JNIRN app = new JNIRN();

        JCommander jCommander = new JCommander(app);
        jCommander.setProgramName("jnirn");
        jCommander.parse(args);

        if (app.help) {
            jCommander.usage();
        }
        else {
            app.run();
        }
    }

    @Parameter
    private List<File> inputFiles;

    @Parameter(names="-o", description = "file in which to generate c source to register JNI native methods")
    private String outputCSourceFile = null;

    @Parameter(names="-f", description = "name of the generated function that registers JNI native methods")
    private String functionName = "RegisterNatives";

    @Parameter(names="-M", description = "file in which to generate dependency rules in make syntax")
    private String outputMakefile = null;

    @Parameter(names={"-h","--help", "-?"}, description = "show this help", hidden = true)
    private boolean help = false;

    public void run() throws IOException {
        JavaBytecodeParser parser = new JavaBytecodeParser();
        for (File inputFile : inputFiles) {
            parser.parse(inputFile);
        }

        if (outputCSourceFile != null) {
            CSourceOutput cSourceOutput = new CSourceOutput();
            OutputStream out = outputCSourceFile.equals("-") ? System.out : new FileOutputStream(outputCSourceFile);

            try {
                PrintWriter writer = new PrintWriter(out);
                cSourceOutput.writeTo(writer, parser.nativeMethodsByClass());
                writer.flush();
            } finally {
                if (out != System.out) out.close();
            }
        }
    }
}
