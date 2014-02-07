package com.natpryce.jnirn.analysis;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class ClassAnalysis extends ClassVisitor {
    public boolean isInstantiatedFromNativeCode = false;

    public final List<Method> nativeMethodsByName = newArrayList();
    public final List<Method> callbackMethodsByName = newArrayList();

    public final Multimap<String, Method> allMethods = LinkedHashMultimap.create();

    private final Set<String> callbackAnnotations;
    private final Set<String> instantiatedClasses;
    private final Set<String> instantiatedAnnotations;

    public ClassAnalysis(Set<String> callbackAnnotations, Set<String> instantiatedClasses, Set<String> instantiatedAnnotations) {
        super(Opcodes.ASM4);
        this.callbackAnnotations = callbackAnnotations;
        this.instantiatedClasses = instantiatedClasses;
        this.instantiatedAnnotations = instantiatedAnnotations;
    }

    public boolean classInterfacesWithNativeCode() {
        return isInstantiatedFromNativeCode || !nativeMethodsByName.isEmpty() || !callbackMethodsByName.isEmpty();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isInstantiatedFromNativeCode |= instantiatedClasses.contains(Type.getObjectType(name).getClassName());
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        isInstantiatedFromNativeCode |= instantiatedAnnotations.contains(desc);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        Method methodInfo = new Method(name, desc);

        if ((access & Opcodes.ACC_NATIVE) != 0) {
            allMethods.put(name, methodInfo);
            nativeMethodsByName.add(methodInfo);
            return null;
        }
        else if ((access & Opcodes.ACC_STATIC) != 0) {
            // Callbacks only supported for static methods at the moment, which means we don't have to worry about
            // inheritance and interfaces
            return new MethodAnalysis(methodInfo);
        }
        else {
            return null;
        }
    }

    private class MethodAnalysis extends MethodVisitor {
        private final Method methodInfo;

        public MethodAnalysis(Method methodInfo) {
            super(ClassAnalysis.this.api);
            this.methodInfo = methodInfo;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            Type annotationType = Type.getType(desc);

            if (callbackAnnotations.contains(annotationType.getClassName())) {
                allMethods.put(methodInfo.getName(), methodInfo);
                callbackMethodsByName.add(methodInfo);
            }

            return null;
        }
    }
}
