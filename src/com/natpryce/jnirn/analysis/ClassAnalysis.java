package com.natpryce.jnirn.analysis;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import java.util.Set;

public class ClassAnalysis extends ClassVisitor {
    public boolean isInstantiatedFromNativeCode = false;
    public final Multimap<String, Method> nativeMethodsByName = LinkedHashMultimap.create();
    public final Multimap<String, Method> callbackMethodsByName = LinkedHashMultimap.create();

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
            recordNativeMethod(methodInfo);
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

    private boolean recordNativeMethod(Method methodInfo) {
        return nativeMethodsByName.put(methodInfo.getName(), methodInfo);
    }

    private void recordCallbackMethod(Method methodInfo) {
        callbackMethodsByName.put(methodInfo.getName(), methodInfo);
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
                recordCallbackMethod(methodInfo);
            }

            return null;
        }
    }
}
