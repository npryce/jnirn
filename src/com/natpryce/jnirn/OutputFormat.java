package com.natpryce.jnirn;

import java.io.PrintWriter;

public interface OutputFormat {
    void writeTo(PrintWriter writer, Iterable<ParsedClass> classes);
}
