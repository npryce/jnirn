package com.natpryce.jnirn.examples;

import java.math.BigInteger;

public class ClassWithNativeMethods {
    public static native void simpleStatic();
    public native void simpleNonStatic();
    public native void method_With_Underscores();

    public static native String methodWithParametersAndResult(int anInt, BigInteger aBigInt);
}
