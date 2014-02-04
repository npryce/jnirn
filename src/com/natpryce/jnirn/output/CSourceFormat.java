package com.natpryce.jnirn.output;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.natpryce.jnirn.OutputFormat;
import com.natpryce.jnirn.ParsedClass;
import com.natpryce.jnirn.ParsedMethod;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.String;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class CSourceFormat implements OutputFormat {
    private final String modulePrefix;
    private final Template template;
    private final Optional<String> headerFileName;

    public CSourceFormat(Template template, String modulePrefix, Optional<String> headerFileName) {
        this.template = template;
        this.modulePrefix = modulePrefix;
        this.headerFileName = headerFileName;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        List<ParsedClass> classesWithNativeMethods = newArrayList(Iterables.filter(classes, ParsedClass.hasNativeMethods));
        List<ParsedClass> classesWithCallbackMethods = newArrayList(Iterables.filter(classes, ParsedClass.hasCallbackMethods));

        Map<String, Object> model = newHashMap();

        model.put("headerfiles", newArrayList(Iterables.transform(headerFileName.asSet(), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return new File(input).getName();
            }
        })));
        model.put("module", modulePrefix);
        model.put("natives", classesWithNativeMethods);
        model.put("callbacks", classesWithCallbackMethods);

        template.execute(model, writer);
    }
}
