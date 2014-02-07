package com.natpryce.jnirn.proguard;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ClassMethodMapperTest {

    private List<String> nothing = newArrayList();
    private MethodMaker maker = new MethodMaker();
    private ClassMethodMapper classMethodMapper;

    @Before
    public void setUp() throws Exception {
        List<String> lines = Resources.readLines(Resources.getResource(getClass(), "jnirn.map.txt"), Charsets.UTF_8);

        Map<String, MapFileParser.MangledClass> parsed = new MapFileParser().parse(lines);

        classMethodMapper = new ClassMethodMapper(parsed);
    }

    @Test
    public void mappingMethodWithNoParameters() throws Exception {
        ClassMethod mapped = classMethodMapper.mapMethod(new ClassMethod("com/beust/jcommander/Parameter", maker.descriptorFor("listConverter", "java.lang.Class", nothing)));
        assertThat(mapped.className, equalTo("k"));
        assertThat(mapped.method, equalTo(maker.descriptorFor("h", "java.lang.Class", nothing)));
    }

    @Test
    public void mappingMethodWithJavaLangParameters() throws Exception {
        ClassMethod mapped = classMethodMapper.mapMethod(
                new ClassMethod(
                        "com/beust/jcommander/converters/BigDecimalConverter",
                        maker.descriptorFor("convert", "java.math.BigDecimal", newArrayList("java.lang.String"))
                )
        );
        assertThat(mapped.className, equalTo("s"));
        assertThat(mapped.method, equalTo(maker.descriptorFor("b", "java.math.BigDecimal", newArrayList("java.lang.String"))));
    }

    @Test
    public void mappingMethodWithMappedParameters() throws Exception {
        ClassMethod mapped = classMethodMapper.mapMethod(
                new ClassMethod(
                        "com/samskivert/mustache/Mustache$BlockSegment",
                        maker.descriptorFor("executeSegs", "void", newArrayList(
                                "com.samskivert.mustache.Template",
                                "com.samskivert.mustache.Template$Context",
                                "java.io.Writer"
                        ))
                )
        );
        assertThat(mapped.className, equalTo("ce"));
        assertThat(mapped.method, equalTo(maker.descriptorFor("a_", "void", newArrayList("cv", "cx", "java.io.Writer"))));
    }


    //com.samskivert.mustache.Template$Fragment createFragment(com.samskivert.mustache.Template$Segment[],com.samskivert.mustache.Template$Context) -> a
    @Test
    public void mappingMethodWithMappedParametersAndReturnValuesWithArrays() throws Exception {
        ClassMethod mapped = classMethodMapper.mapMethod(
                new ClassMethod(
                        "com/samskivert/mustache/Template",
                        maker.descriptorFor("createFragment", "com.samskivert.mustache.Template$Fragment", newArrayList(
                                "com.samskivert.mustache.Template$Segment[]",
                                "com.samskivert.mustache.Template$Context"
                        ))
                )
        );
        assertThat(mapped.className, equalTo("cv"));
        assertThat(mapped.method, equalTo(maker.descriptorFor("a", "cy", newArrayList("cA[]", "cx"))));
    }
}
