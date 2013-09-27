package com.natpryce.jnirn.examples;

import java.math.BigInteger;

public class AClassWithOverloadedNativeMethods {
    public static native void nativeMethod();
    public static native void nativeMethod(int a, int b);
    public static native void nativeMethod(String s);
    public static native void nativeMethod(String s, String t);
    public static native void nativeMethod(BigInteger i1, BigInteger i2, BigInteger i3);

    public static native void native_Method_With_Underscores();
    public static native void native_Method_With_Underscores(int a, int b);
    public static native void native_Method_With_Underscores(String s, String t);
}
