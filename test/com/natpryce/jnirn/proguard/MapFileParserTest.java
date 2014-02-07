package com.natpryce.jnirn.proguard;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class MapFileParserTest {


    private Map<String,MapFileParser.MangledClass> parsed;

    @Before
    public void readTheMapFile() throws Exception {
        List<String> lines = Resources.readLines(Resources.getResource(getClass(), "jnirn.map.txt"), Charsets.UTF_8);

        parsed = new MapFileParser().parse(lines);
    }

    @Test
    public void testCanParseClassesFromAMapFile() throws Exception {

        String classNameKnownToBeInTheMapFile = "org/objectweb/asm/ClassReader";

        assertThat(parsed, hasKey(classNameKnownToBeInTheMapFile));
        MapFileParser.MangledClass someClassKnownToBeIntheMapFile = parsed.get(classNameKnownToBeInTheMapFile);

        assertThat(someClassKnownToBeIntheMapFile.mangledClassName, equalTo("cF"));

        Map<String, MapFileParser.MangledMethod> methods = someClassKnownToBeIntheMapFile.methods;
        String methodSignatureKnownToBeInTheClass = "readClass(Ljava/io/InputStream;Z)[B";
        assertThat(methods, hasKey(methodSignatureKnownToBeInTheClass));
        assertThat(methods.get(methodSignatureKnownToBeInTheClass).mangledName, equalTo("a"));
    }

    @Test
    public void testIncludesClassesEvenWhenTheHaveNoFieldsOrMethods() throws Exception {

        // class has no methods
        assertThat(parsed, hasKey("com/samskivert/mustache/Template$Fragment"));

        //class has no fields or methods
        assertThat(parsed, hasKey("com/google/common/collect/SetMultimap"));
    }
}
