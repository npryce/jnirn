package com.natpryce.jnirn.proguard;

import org.junit.Test;
import org.objectweb.asm.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JavaTypeConverterTest {

    JavaTypeConverter converter = new JavaTypeConverter();

    @Test
    public void testBasicTypes() throws Exception {
        assertThat(converter.type("int"), equalTo(Type.getType("I")));
        assertThat(converter.type("float"), equalTo(Type.getType("F")));
        assertThat(converter.type("boolean"), equalTo(Type.getType("Z")));
        assertThat(converter.type("double"), equalTo(Type.getType("D")));
        assertThat(converter.type("void"), equalTo(Type.getType("V")));
        assertThat(converter.type("long"), equalTo(Type.getType("J")));
    }

    @Test
    public void testStandardJavaTypes() throws Exception {
        assertThat(converter.type("java.lang.String"), equalTo(Type.getType("Ljava/lang/String;")));
    }

    @Test
    public void testSomeClassThatWeCantActuallyLoad() throws Exception {
        assertThat(converter.type("com.natpryce.bobble.NotExist"), equalTo(Type.getType("Lcom/natpryce/bobble/NotExist;")));
        assertThat(converter.type("com.natpryce.bobble.NotExist$1"), equalTo(Type.getType("Lcom/natpryce/bobble/NotExist$1;")));
        assertThat(converter.type("com.natpryce.bobble.NotExist$Jar"), equalTo(Type.getType("Lcom/natpryce/bobble/NotExist$Jar;")));
    }

    @Test
    public void testBasicArrayType() throws Exception {
        assertThat(converter.type("int[]"), equalTo(Type.getType("[I")));
        assertThat(converter.type("int[][]"), equalTo(Type.getType("[[I")));
    }

    @Test
    public void testClassArrayType() throws Exception {
        assertThat(converter.type("java.lang.Object[]"), equalTo(Type.getType("[Ljava/lang/Object;")));
        assertThat(converter.type("java.lang.String[][]"), equalTo(Type.getType("[[Ljava/lang/String;")));
    }
}
