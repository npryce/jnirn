/* GENERATED CODE - DO NOT EDIT */

#include <jni.h>
#include "com_natpryce_jnirn_examples_AnotherClassWithNativeMethods.h"
#include "com_natpryce_jnirn_examples_ClassWithNativeMethods.h"

static const JNINativeMethod method_table_com_natpryce_jnirn_examples_AnotherClassWithNativeMethods[] = {
   {"nativeMethodOne", "()I", "Java_com_natpryce_jnirn_examples_AnotherClassWithNativeMethods_nativeMethodOne"},
   {"nativeMethodTwo", "()I", "Java_com_natpryce_jnirn_examples_AnotherClassWithNativeMethods_nativeMethodTwo"}
};
static int method_count_com_natpryce_jnirn_examples_AnotherClassWithNativeMethods = 2;

static const JNINativeMethod method_table_com_natpryce_jnirn_examples_ClassWithNativeMethods[] = {
   {"simpleStatic", "()V", "Java_com_natpryce_jnirn_examples_ClassWithNativeMethods_simpleStatic"},
   {"simpleNonStatic", "()V", "Java_com_natpryce_jnirn_examples_ClassWithNativeMethods_simpleNonStatic"},
   {"methodWithParametersAndResult", "(ILjava/math/BigInteger;)Ljava/lang/String;", "Java_com_natpryce_jnirn_examples_ClassWithNativeMethods_methodWithParametersAndResult"}
};
static int method_count_com_natpryce_jnirn_examples_ClassWithNativeMethods = 3;


jint RegisterNatives(JNIEnv *env) {
    jclass the_class;
    jint status;

    the_class = (*env)->FindClass(env, "com/natpryce/jnirn/examples/AnotherClassWithNativeMethods");
    if (the_class == NULL) return -1;
    status = (*env)->RegisterNatives(env, the_class, method_table_com_natpryce_jnirn_examples_AnotherClassWithNativeMethods, method_count_com_natpryce_jnirn_examples_AnotherClassWithNativeMethods);
    if (status < 0) return status;

    the_class = (*env)->FindClass(env, "com/natpryce/jnirn/examples/ClassWithNativeMethods");
    if (the_class == NULL) return -1;
    status = (*env)->RegisterNatives(env, the_class, method_table_com_natpryce_jnirn_examples_ClassWithNativeMethods, method_count_com_natpryce_jnirn_examples_ClassWithNativeMethods);
    if (status < 0) return status;

    return 0;
}
