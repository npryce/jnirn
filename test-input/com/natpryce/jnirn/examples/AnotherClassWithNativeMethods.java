package com.natpryce.jnirn.examples;

public class AnotherClassWithNativeMethods {

    public static class ArgClass {}
    public static class RetClass {}

    public native int nativeMethodOne();
    public native int nativeMethodTwo();
    public native RetClass nativeMethodThree(ArgClass a);
}
