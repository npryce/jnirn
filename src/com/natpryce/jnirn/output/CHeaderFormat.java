package com.natpryce.jnirn.output;

import com.natpryce.jnirn.OutputFormat;
import com.natpryce.jnirn.ParsedClass;

import java.io.PrintWriter;

public class CHeaderFormat implements OutputFormat {
    private final String modulePrefix;

    public CHeaderFormat(String modulePrefix) {
        this.modulePrefix = modulePrefix;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        String guardDefine = modulePrefix + "_HEADER";

        writer.println("#ifndef " + guardDefine);
        writer.println("#define " + guardDefine);
        writer.println();
        writer.println("/* GENERATED CODE - DO NOT EDIT */");
        writer.println();
        writer.println("#include <jni.h>");
        writer.println();
        writer.println("jint " + modulePrefix + "Init(JNIEnv*);");
        writer.println();
        writer.println("#endif");
    }
}
