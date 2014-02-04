package com.natpryce.jnirn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.natpryce.jnirn.analysis.JavaBytecodeParser;
import com.natpryce.jnirn.output.CHeaderFormat;
import com.natpryce.jnirn.output.CSourceFormat;
import com.natpryce.jnirn.output.FileOutput;
import com.natpryce.jnirn.output.MakeDependencyFormat;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.*;
import java.util.List;
import java.util.Set;

import static com.beust.jcommander.internal.Lists.newArrayList;
import static com.beust.jcommander.internal.Sets.newHashSet;

public class JNIRN {
    public static void main(String... args) throws IOException {
        JNIRN app = new JNIRN();

        JCommander jCommander = new JCommander(app);
        jCommander.setProgramName("jnirn");
        jCommander.parse(args);

        if (app.help) {
            jCommander.usage();
        } else {
            app.run();
        }
    }

    @Parameter
    public List<File> inputFiles = newArrayList();

    @Parameter(names = "-ct", description = "mustache template for C source file")
    public String cSourceFileTemplate = null;

    @Parameter(names = "-Ht", description = "mustache template for C header file")
    public String cHeaderFileTemplate = null;

    @Parameter(names = "-o", description = "name of generated C source file")
    public String outputCSourceFile = null;

    @Parameter(names = "-H", description = "name of generated C header file")
    public String outputCHeaderFile = null;

    @Parameter(names = "-p", description = "prefix for all globally visible symbols")
    public String modulePrefix = "Natives";

    @Parameter(names = "-M", description = "file in which to generate dependency rules in make syntax (requires -o)")
    public String outputMakefile = null;

    @Parameter(names = {"-C", "--callback-annotation"}, description = "FQN of an annotation used to identify Java methods called from native code")
    public Set<String> callbackAnnotations = newHashSet();

    @Parameter(names = {"-I", "--instantiated-annotation"}, description = "FQN of an annotation used to identify classes instantiated from native code")
    public Set<String> instantiatedAnnotations = newHashSet();

    @Parameter(names = {"-i", "--instantiated-class"}, description = "FQN of a class instantiated from native code")
    public Set<String> instantiatedClasses = newHashSet();

    @Parameter(names = {"-h", "--help", "-?"}, description = "show this help", hidden = true)
    public boolean help = false;

    public void run() throws IOException {
        if (outputMakefile != null && outputCSourceFile == null) {
            throw new ParameterException("cannot generate Makefile dependencies if no output file name specified");
        }

        JavaBytecodeParser parser = new JavaBytecodeParser(
                ImmutableSet.copyOf(callbackAnnotations),
                ImmutableSet.copyOf(instantiatedAnnotations),
                ImmutableSet.copyOf(instantiatedClasses));

        parser.parseAll(inputFiles);

        for (Output output : outputs()) {
            parser.writeTo(output);
        }
    }

    private Iterable<Output> outputs() throws FileNotFoundException {
        List<Output> outputs = newArrayList();

        if (outputCSourceFile != null) {
            outputs.add(new FileOutput(outputCSourceFile,
                    new CSourceFormat(
                            loadTemplateOrDefault(cSourceFileTemplate, CSourceFormat.class, "c-source-template.txt"),
                            modulePrefix, Optional.fromNullable(outputCHeaderFile)
                    ))
            );
        }

        if (outputCHeaderFile != null) {
            outputs.add(new FileOutput(outputCHeaderFile,
                    new CHeaderFormat(
                            loadTemplateOrDefault(cHeaderFileTemplate, CHeaderFormat.class, "c-header-template.txt"),
                            modulePrefix))
            );
        }

        if (outputMakefile != null) {
            outputs.add(new FileOutput(outputMakefile, new MakeDependencyFormat(outputCSourceFile)));
        }

        return outputs;
    }

    private Template loadTemplateOrDefault(String parameterValue, Class<?> owner, String resourceName) throws FileNotFoundException {
        return parameterValue == null ? fromClass(owner, resourceName) : fromFile(parameterValue);
    }


    private Template fromClass(Class<?> owner, String resourceName) {
        InputStream resource = owner.getResourceAsStream(resourceName);
        return fromReader(new InputStreamReader(resource));
    }

    private Template fromFile(String filePath) throws FileNotFoundException {
        return fromReader(new FileReader(filePath));
    }

    private Template fromReader(InputStreamReader source) {
        try {
            return Mustache.compiler().compile(source);
        } finally {
            closeQuietly(source);
        }
    }

    private static void closeQuietly(InputStreamReader source) {
        try {
            source.close();
        } catch (IOException e) {

        }
    }

}
