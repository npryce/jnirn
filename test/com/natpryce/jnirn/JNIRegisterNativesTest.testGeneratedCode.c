/* GENERATED CODE - DO NOT EDIT */

#include <jni.h>
#include "com_natpryce_jnirn_examples_ClassWithNativeMethods.h"

static const JNINativeMethod methods_com_natpryce_jnirn_examples_ClassWithNativeMethods[] = {
   {"simpleStatic", "()V", "Java_com_natpryce_jnirn_examples_ClassWithNativeMethods_simpleStatic"},
   {"simpleNonStatic", "()V", "Java_com_natpryce_jnirn_examples_ClassWithNativeMethods_simpleNonStatic"}
};


jint RegisterNatives(JNIEnv *env) {
    jclass the_class;
    jint status;

    the_class = (*env)->FindClass(env, "com/natpryce/jnirn/examples/ClassWithNativeMethods");
    if (the_class == NULL) return -1;
    status = (*env)->RegisterNatives(env, the_class, methods_com_natpryce_jnirn_examples_ClassWithNativeMethods, 2);
    if (status < 0) return status;

    return 0;
}
