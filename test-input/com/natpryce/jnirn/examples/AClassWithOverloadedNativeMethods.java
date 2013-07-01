package com.natpryce.jnirn.examples;

public class AClassWithOverloadedNativeMethods {
    public static native void nativeMethod();
    public static native void nativeMethod(int a, int b);
    public static native void nativeMethod(String s);
    public static native void nativeMethod(String s, String t);
}
