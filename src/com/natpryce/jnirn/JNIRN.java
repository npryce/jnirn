package com.natpryce.jnirn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.natpryce.jnirn.analysis.JavaBytecodeParser;
import com.natpryce.jnirn.output.*;

import java.io.File;
import java.io.IOException;
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
        }
        else {
            app.run();
        }
    }

    @Parameter
    public List<File> inputFiles = newArrayList();

    @Parameter(names="-o", description = "name of generated C source file")
    public String outputCSourceFile = null;

    @Parameter(names="-H", description = "name of generated C header file")
    public String outputCHeaderFile = null;

    @Parameter(names="-p", description = "prefix for all globally visible symbols")
    public String modulePrefix = "Natives";

    @Parameter(names="-M", description = "file in which to generate dependency rules in make syntax (requires -o)")
    public String outputMakefile = null;

    @Parameter(names={"-C", "--callback-annotation"}, description = "FQN of an annotation used to identify Java methods called from native code")
    public Set<String> callbackAnnotations = newHashSet();

    @Parameter(names={"-I", "--instantiated-annotation"}, description = "FQN of an annotation used to identify classes instantiated from native code")
    public Set<String> instantiatedAnnotations = newHashSet();

    @Parameter(names={"-i", "--instantiated-class"}, description = "FQN of a class instantiated from native code")
    public Set<String> instantiatedClasses = newHashSet();

    @Parameter(names={"-h","--help", "-?"}, description = "show this help", hidden = true)
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

    private Iterable<Output> outputs() {
        List<Output> outputs = newArrayList();

        if (outputCSourceFile != null) {
            outputs.add(new FileOutput(outputCSourceFile, new CSourceFormat(modulePrefix, Optional.fromNullable(outputCHeaderFile))));
        }

        if (outputCHeaderFile != null) {
            outputs.add(new FileOutput(outputCHeaderFile, new CHeaderFormat(modulePrefix)));
        }

        if (outputMakefile != null) {
            outputs.add(new FileOutput(outputMakefile, new MakeDependencyFormat(outputCSourceFile)));
        }

        return outputs;
    }
}
