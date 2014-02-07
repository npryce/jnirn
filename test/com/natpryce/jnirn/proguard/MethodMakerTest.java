package com.natpryce.jnirn.proguard;

import org.junit.Test;
import org.objectweb.asm.commons.Method;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MethodMakerTest {

    private static final List<String> NO = Collections.emptyList();

    private final MethodMaker methodMaker = new MethodMaker();

    @Test
    public void testConvertVoidNoArg() throws Exception {
        assertThat(descriptor("void", "size", NO), equalTo(new Method("size","()V")));
    }

    @Test
    public void testConvertPrimitiveSinglePrimitiveArg() throws Exception {
        assertThat(descriptor("int", "size", newArrayList("int")), equalTo(new Method("size", "(I)I")));
    }

    @Test
    public void testConvertSomethingABitMoreComplicated() throws Exception {
        assertThat(
                descriptor("org.objectweb.asm.Attribute", "size",
                        newArrayList("java.lang.String", "int", "int", "char[]", "int", "org.objectweb.asm.Label[]" )),
                equalTo(new Method("size", "(Ljava/lang/String;II[CI[Lorg/objectweb/asm/Label;)Lorg/objectweb/asm/Attribute;")));
    }

    private Method descriptor(String returnType, String name, List<String> parameterTypes) {
        return methodMaker.descriptorFor(name, returnType, parameterTypes);
    }
}
