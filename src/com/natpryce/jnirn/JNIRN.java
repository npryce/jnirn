package com.natpryce.jnirn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.io.IOException;
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
    public  String outputCSourceFile;

    @Parameter(names="-f", description = "name of the generated function that registers JNI native methods")
    public  String functionName = "RegisterNatives";

    @Parameter(names="-M", description = "file in which to generate dependency rules in make syntax (requires -o)")
    public  String outputMakefile = null;

    @Parameter(names={"-h","--help", "-?"}, description = "show this help", hidden = true)
    public  boolean help = false;


    public void run() throws IOException {
        if (outputMakefile != null && outputCSourceFile == null) {
            throw new ParameterException("cannot generate Makefile dependencies if no output file name specified");
        }

        JavaBytecodeParser parser = new JavaBytecodeParser();
        parser.parseAll(inputFiles);
        for (Output output : outputs()) {
            parser.writeTo(output);
        }
    }

    private Iterable<Output> outputs() {
        List<Output> outputs = newArrayList();

        if (outputCSourceFile != null) {
            outputs.add(new FileOutput(outputCSourceFile, new CSourceFormat(functionName)));
        }
        else {
            outputs.add(new StdioOutput(System.out, new CSourceFormat(functionName)));
        }

        if (outputMakefile != null) {
            outputs.add(new FileOutput(outputMakefile, new MakeDependencyFormat(outputCSourceFile)));
        }

        return outputs;
    }
}
