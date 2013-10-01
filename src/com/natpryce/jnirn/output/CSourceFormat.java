package com.natpryce.jnirn.output;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.natpryce.jnirn.OutputFormat;
import com.natpryce.jnirn.ParsedClass;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CSourceFormat implements OutputFormat {
    private final String modulePrefix;
    private final Optional<String> headerFileName;

    public CSourceFormat(String modulePrefix, Optional<String> headerFileName) {
        this.modulePrefix = modulePrefix;
        this.headerFileName = headerFileName;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        List<ParsedClass> classesWithNativeMethods = newArrayList(Iterables.filter(classes, ParsedClass.hasNativeMethods));
        List<ParsedClass> classesWithCallbackMethods = newArrayList(Iterables.filter(classes, ParsedClass.hasCallbackMethods));

        writer.println("/* GENERATED CODE - DO NOT EDIT */");
        writer.println();
        writeIncludeDirectives(writer, classesWithNativeMethods);
        writer.println();
        writeNativeMethodTables(writer, classesWithNativeMethods);
        writer.println();
        if (!classesWithCallbackMethods.isEmpty()) {
            writeGlobalClassReferences(writer, classesWithCallbackMethods);
            writer.println();
            writeGlobalCallbackVariables(writer, classesWithCallbackMethods);
            writer.println();
            writeGlobalCallbackTable(writer, classesWithCallbackMethods);
        }
        writeInitialisationFunction(writer, classesWithNativeMethods, classesWithCallbackMethods);
    }

    private void writeIncludeDirectives(PrintWriter writer, Iterable<ParsedClass> classes) {
        writer.println("#include <jni.h>");

        for (ParsedClass c : classes) {
            writeIncludeGeneratedHeader(writer, c);
        }

        for (String f : headerFileName.asSet()) {
            writer.println("#include \"" + new File(f).getName() + "\"");
        }
    }

    private void writeIncludeGeneratedHeader(PrintWriter writer, ParsedClass c) {
        writer.println("#include \"" + jniGeneratedHeaderForClass(c.name) + ".h\"");
    }

    private void writeNativeMethodTables(PrintWriter writer, Iterable<ParsedClass> classes) {
        writeJNINativesArrays(writer, classes);
        writer.println();
        writeRegistrationList(writer, classes);
    }

    private void writeJNINativesArrays(PrintWriter writer, Iterable<ParsedClass> classes) {
        for (ParsedClass c : classes) {
            writeJNINativeMethodTable(writer, c.name, c.nativeMethods);
        }
    }

    private void writeJNINativeMethodTable(PrintWriter writer, String className, Multimap<String, Method> nativeMethods) {
        writer.println("static const JNINativeMethod " + methodTableNameForClass(className) + "[] = {");

        boolean separatorRequired = false;

        for (String methodName : nativeMethods.keySet()) {
            Collection<Method> overloads = nativeMethods.get(methodName);
            boolean isOverloaded = overloads.size() > 1;

            for (Method nativeMethod : overloads) {
                if (separatorRequired) {
                    writer.println(",");
                }

                writer.print("   {\"" + nativeMethod.getName() + "\", " +
                        "\"" + nativeMethod.getDescriptor() + "\", " +
                        cFunctionNameForNativeMethod(className, nativeMethod, isOverloaded) + "}");

                separatorRequired = true;
            }
        }

        writer.println();
        writer.println("};");
    }

    private void writeRegistrationList(PrintWriter writer, Iterable<ParsedClass> classes) {

        writer.println("struct registration { const char * class_name; const JNINativeMethod *methods; int method_count; };");

        writer.println("static const struct registration registrations[] = {");
        for (ParsedClass c : classes) {
            writer.println("    {\"" + c.name + "\", " + methodTableNameForClass(c.name) + ", " + c.nativeMethods.size() + "},");
        }
        writer.println("};");
    }

    private void writeGlobalCallbackVariables(PrintWriter writer, List<ParsedClass> classes) {
        for (ParsedClass c : classes) {
            for (String methodName : c.callbackMethods.keySet()) {
                ImmutableCollection<Method> overloads = c.callbackMethods.get(methodName);
                if (overloads.size() > 1) {
                    throw new IllegalStateException("overloaded callback methods not yet supported");
                }

                writeGlobalCallbackVariable(writer, c, Iterables.getOnlyElement(overloads));
            }
        }
    }

    private void writeGlobalClassReferences(PrintWriter writer, List<ParsedClass> classes) {
        for (ParsedClass c : classes) {
            if (!c.callbackMethods.isEmpty()) {
                writeGlobalClassReference(writer, c);
            }
        }
    }

    private void writeGlobalClassReference(PrintWriter writer, ParsedClass c) {
        writer.println("jclass " + variableNameForClassReference(c) + ";");
    }

    private void writeGlobalCallbackVariable(PrintWriter writer, ParsedClass c, Method m) {
        writer.println("jmethodID " + variableNameForMethodId(c, m) + ";");
    }

    private void writeGlobalCallbackTable(PrintWriter writer, List<ParsedClass> classes) {
        writer.println("struct method_ref { const char *name; const char *descriptor; jmethodID *method_id_p; };");
        writer.println("struct class_ref { const char *name; jobject *global_ref_p; int method_count; const struct method_ref* methods; };");

        writer.println("static const struct class_ref referenced_classes[] = {");

        for (ParsedClass c : classes) {
            ImmutableCollection<Method> methods = c.callbackMethods.values();

            writer.println("    { \"" + c.name + "\", &" + variableNameForClassReference(c) + ", " + c.callbackMethods.size() + ",");
            writer.println("        (struct method_ref[]) {");
            for (Method m : methods) {
                writer.println("            { \"" + m.getName() + "\", \"" + m.getDescriptor() + "\", &" + variableNameForMethodId(c, m) + " },");
            }
            writer.println("        },");
            writer.println("    },");
        }

        writer.println("};");
    }

    private void writeInitialisationFunction(PrintWriter writer, List<ParsedClass> classesWithNativeMethods, List<ParsedClass> classesWithCallbackMethods) {
        writer.println("jint " + modulePrefix + "Init(JNIEnv *env) {");
        writer.println("    jclass the_class;");
        writer.println("    jint status;");
        writer.println("    int i, j;");
        writer.println();
        writer.println("    for (i = 0; i < " + classesWithNativeMethods.size() + "; i++) {");
        writer.println("        the_class = (*env)->FindClass(env, registrations[i].class_name);");
        writer.println("        if (the_class == NULL) return -1;");
        writer.println("        status = (*env)->RegisterNatives(env, the_class, registrations[i].methods, registrations[i].method_count);");
        writer.println("        if (status < 0) return status;");
        writer.println("    }");
        writer.println();

        if (!classesWithCallbackMethods.isEmpty()) {
            writer.println("    for (i = 0; i < " + classesWithCallbackMethods.size() + "; i++) {");
            writer.println("        the_class = (*env)->FindClass(env, referenced_classes[i].name);");
            writer.println("        if (the_class == NULL) return -1;");
            writer.println("        *(referenced_classes[i].global_ref_p) = (*env)->NewGlobalRef(env, the_class);");
            writer.println("        if (*(referenced_classes[i].global_ref_p) == NULL) return -1;");
            writer.println("        for (j = 0; j < referenced_classes[i].method_count; j++) {");
            writer.println("            *(referenced_classes[i].methods[j].method_id_p) = (*env)->GetStaticMethodID(env, the_class, referenced_classes[i].methods[j].name, referenced_classes[i].methods[j].descriptor);");
            writer.println("            if(*(referenced_classes[i].methods[j].method_id_p) == NULL) return -1;");
            writer.println("        }");
            writer.println("    }");
            writer.println();
        }

        writer.println("    return 0;");
        writer.println("}");
    }

    private String cFunctionNameForNativeMethod(String className, Method nativeMethod, boolean isOverloaded) {
        String baseName = "Java_" + classNameToCIndentifier(className) + "_" + nativeMethod.getName().replace("_", "_1");
        if (isOverloaded) {
            return baseName + "__" + signatureToC(nativeMethod.getArgumentTypes());
        } else {
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

    private String variableNameForMethodId(ParsedClass c, Method method) {
        return modulePrefix + "_method_" + classNameToCIndentifier(c.name) + "__" + method.getName();
    }

    private String variableNameForClassReference(ParsedClass c) {
        return modulePrefix + "_class_" + classNameToCIndentifier(c.name);
    }

    private String classNameToCIndentifier(String className) {
        return className.replace("/", "_");
    }
}
