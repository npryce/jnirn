package com.natpryce.jnirn.output;

import com.natpryce.jnirn.OutputFormat;
import com.natpryce.jnirn.ParsedClass;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CHeaderFormat implements OutputFormat {
    private final String modulePrefix;
    private final Template template;

    public CHeaderFormat(Template template, String modulePrefix) {
        this.template = template;
        this.modulePrefix = modulePrefix;
    }

    @Override
    public void writeTo(PrintWriter writer, Iterable<ParsedClass> classes) {
        Map<String,String> model = new HashMap<String,String>();
        model.put("modulePrefix", modulePrefix);
        template.execute(model, writer);
    }
}
