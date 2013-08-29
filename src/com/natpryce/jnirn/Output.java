package com.natpryce.jnirn;

import java.io.IOException;

public interface Output {
    void write(Iterable<ParsedClass> classes) throws IOException;
}
