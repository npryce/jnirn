package com.natpryce.jnirn.examples;

public class ClassWithNativeAndCallbackMethods {
    private static native void nativeMethod();

    @NativeCallback
    private static void calledFromNativeCode() {
    }
}
