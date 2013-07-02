package com.natpryce.jnirn;

import org.objectweb.asm.Type;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class CSourceOutput {
    private final String publicFunctionName;

    public CSourceOutput(String publicFunctionName) {
        this.publicFunctionName = publicFunctionName;
    }

    public void writeTo(PrintWriter writer, SortedMap<String, Map<String, List<NativeMethod>>> nativeMethodsByClass) {
        writer.println("/* GENERATED CODE - DO NOT EDIT */");
        writer.println();
        writeIncludeDirectives(writer, nativeMethodsByClass);
        writer.println();
        writeNativeMethodTables(writer, nativeMethodsByClass);
        writer.println();
        writeRegisterNativesCalls(writer, nativeMethodsByClass);
    }

    private void writeIncludeDirectives(PrintWriter writer, SortedMap<String, Map<String, List<NativeMethod>>> nativeMethodsByClass) {
        writer.println("#include <jni.h>");
        for (String className: nativeMethodsByClass.keySet()) {
            writeIncludeGeneratedHeader(writer, className);
        }
    }

    private void writeIncludeGeneratedHeader(PrintWriter writer, String className) {
        writer.println("#include \"" + jniGeneratedHeaderForClass(className) + ".h\"");
    }

    private void writeNativeMethodTables(PrintWriter writer, SortedMap<String, Map<String, List<NativeMethod>>> nativeMethodsByClass) {
        for (String className : nativeMethodsByClass.keySet()) {
            writeNativeMethodTable(writer, className, nativeMethodsByClass.get(className));
        }
    }

    private void writeNativeMethodTable(PrintWriter writer, String className, Map<String, List<NativeMethod>> nativeMethods) {
        writer.println("static const JNINativeMethod " + methodTableNameForClass(className) + "[] = {");

        boolean separatorRequired = false;

        for (String methodName : nativeMethods.keySet()) {
            List<NativeMethod> overloads = nativeMethods.get(methodName);
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

    private void writeRegisterNativesCalls(PrintWriter writer, SortedMap<String, Map<String, List<NativeMethod>>> nativeMethodsByClass) {
        //jint RegisterNatives(JNIEnv *env, jclass clazz, const JNINativeMethod *methods, jint nMethods);

        writer.println("jint " + publicFunctionName + "(JNIEnv *env) {");
        writer.println("    jclass the_class;");
        writer.println("    jint status;");
        writer.println();

        for (String className : nativeMethodsByClass.keySet()) {
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
