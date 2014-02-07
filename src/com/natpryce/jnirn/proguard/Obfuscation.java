package com.natpryce.jnirn.proguard;

public interface Obfuscation {
    public static final Obfuscation NULL = new Obfuscation() {
        @Override
        public String mapClass(String className) {
            return className;
        }

        @Override
        public ClassMethod mapMethod(ClassMethod classMethod) {
            return classMethod;
        }
    };

    String mapClass(String className);

    ClassMethod mapMethod(ClassMethod classMethod);
}
