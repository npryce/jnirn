package com.natpryce.jnirn;

import com.google.common.collect.Multimap;
import org.objectweb.asm.Type;

import java.io.PrintWriter;
import java.util.Collection;

public class CSourceFormat implements OutputFormat {
    private final String publicFunctionName;

    public CSourceFormat(String publicFunctionName) {
        this.publicFunctionName = publicFunctionName;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        writer.println("/* GENERATED CODE - DO NOT EDIT */");
        writer.println();
        writeIncludeDirectives(writer, classes);
        writer.println();
        writeNativeMethodTables(writer, classes);
        writer.println();
        writeRegisterNativesCalls(writer, classes);
    }

    private void writeIncludeDirectives(PrintWriter writer, Iterable<ParsedClass> classes) {
        writer.println("#include <jni.h>");
        for (ParsedClass c: classes) {
            writeIncludeGeneratedHeader(writer, c);
        }
    }

    private void writeIncludeGeneratedHeader(PrintWriter writer, ParsedClass c) {
        writer.println("#include \"" + jniGeneratedHeaderForClass(c.name) + ".h\"");
    }

    private void writeNativeMethodTables(PrintWriter writer, Iterable<ParsedClass> classes) {
        for (ParsedClass c : classes) {
            writeNativeMethodTable(writer, c.name, c.nativeMethods);
        }
    }

    private void writeNativeMethodTable(PrintWriter writer, String className, Multimap<String, NativeMethod> nativeMethods) {
        writer.println("static const JNINativeMethod " + methodTableNameForClass(className) + "[] = {");

        boolean separatorRequired = false;

        for (String methodName : nativeMethods.keySet()) {
            Collection<NativeMethod> overloads = nativeMethods.get(methodName);
            boolean isOverloaded = overloads.size() > 1;

            for (NativeMethod nativeMethod : overloads) {
                if (separatorRequired) {
                    writer.println(",");
                }

                writer.print("   {\"" + nativeMethod.name + "\", " +
                        "\"" + nativeMethod.type.getDescriptor() + "\", " +
                        cFunctionNameForNativeMethod(className, nativeMethod, isOverloaded) + "}");

                separatorRequired = true;
            }
        }

        writer.println();
        writer.println("};");
        writer.println("static int " + methodCountNameForClass(className) + " = " + nativeMethods.size() + ";");
        writer.println();
    }

    private void writeRegisterNativesCalls(PrintWriter writer, Iterable<ParsedClass> classes) {
        writer.println("jint " + publicFunctionName + "(JNIEnv *env) {");
        writer.println("    jclass the_class;");
        writer.println("    jint status;");
        writer.println();

        for (ParsedClass c : classes) {
            String className = c.name;

            writer.println("    the_class = (*env)->FindClass(env, \"" + className + "\");");
            writer.println("    if (the_class == NULL) return -1;");
            writer.println("    status = (*env)->RegisterNatives(env, the_class, " + methodTableNameForClass(className) + ", " + methodCountNameForClass(className) + ");");
            writer.println("    if (status < 0) return status;");
            writer.println();
        }

        writer.println("    return 0;");
        writer.println("}");
    }

    private String cFunctionNameForNativeMethod(String className, NativeMethod nativeMethod, boolean isOverloaded) {
        String baseName = "Java_" + classNameToCIndentifier(className) + "_" + nativeMethod.name;
        if (isOverloaded) {
            return baseName + "__" + signatureToC(nativeMethod.argumentTypes());
        }
        else {
            return baseName;
        }
    }

    private String signatureToC(Type[] argumentTypes) {
        StringBuilder b = new StringBuilder();

        for (Type argumentType : argumentTypes) {
            b.append(argumentType.getDescriptor().replace("/", "_").replace(";", "_2"));
        }

        return b.toString();
    }


    private String jniGeneratedHeaderForClass(String className) {
        return classNameToCIndentifier(className);
    }

    private String methodTableNameForClass(String className) {
        return "method_table_" + classNameToCIndentifier(className);
    }

    private String methodCountNameForClass(String className) {
        return "method_count_" + classNameToCIndentifier(className);
    }

    private String classNameToCIndentifier(String className) {
        return className.replace("/", "_");
    }
}
