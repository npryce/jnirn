package com.natpryce.jnirn.examples;

public class ClassWithCallbackMethods {
    @NativeCallback
    private static void callbackA() {
    }

    @NativeCallback
    private static void callbackB() {
    }

    public static class MyClass {}

    @NativeCallback
    private static void callbackC(MyClass arg) {
    }
}
