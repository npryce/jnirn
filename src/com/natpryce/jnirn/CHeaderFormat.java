package com.natpryce.jnirn;

import java.io.PrintWriter;

public class CHeaderFormat implements OutputFormat {
    private final String functionName;

    public CHeaderFormat(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        String guardDefine = functionName + "_HEADER";

        writer.println("#ifndef " + guardDefine);
        writer.println("#define " + guardDefine);
        writer.println();
        writer.println("/* GENERATED CODE - DO NOT EDIT */");
        writer.println();
        writer.println("#include <jni.h>");
        writer.println();
        writer.println("jint " + functionName + "(JNIEnv*);");
        writer.println();
        writer.println("#endif");
    }
}
