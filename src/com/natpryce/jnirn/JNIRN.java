package com.natpryce.jnirn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.*;
import java.util.List;

import static com.beust.jcommander.internal.Lists.newArrayList;

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
    public List<File> inputFiles = newArrayList();

    @Parameter(names="-o", description = "file in which to generate c source to register JNI native methods")
    public  String outputCSourceFile = null;

    @Parameter(names="-f", description = "name of the generated function that registers JNI native methods")
    public  String functionName = "RegisterNatives";

    @Parameter(names="-M", description = "file in which to generate dependency rules in make syntax (requires -o)")
    public  String outputMakefile = null;

    @Parameter(names={"-h","--help", "-?"}, description = "show this help", hidden = true)
    public  boolean help = false;


    public void run() throws IOException {
        JavaBytecodeParser parser = new JavaBytecodeParser();
        for (File inputFile : inputFiles) {
            parser.parse(inputFile);
        }

        CSourceOutput cSourceOutput = new CSourceOutput(functionName);
        Iterable<ParsedClass> nativeClasses = parser.nativeClasses();

        if (outputCSourceFile != null) {
            writeToFile(cSourceOutput, outputCSourceFile, nativeClasses);
            if (outputMakefile != null) {
                MakeDependencyOutput makeDependencyOutput = new MakeDependencyOutput(outputCSourceFile);
                writeToFile(makeDependencyOutput, outputMakefile, nativeClasses);
            }
        }
        else {
            if (outputMakefile != null) {
                throw new IOException("cannot generate Makefile dependencies if no output file name specified");
            }

            PrintWriter writer = new PrintWriter(System.out);
            cSourceOutput.writeTo(writer, nativeClasses);
            writer.flush();
        }
    }

    private void writeToFile(OutputFormat format, String file, Iterable<ParsedClass> classes) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        try {
            format.writeTo(writer, classes);
        }
        finally {
            writer.close();
        }
    }


}
