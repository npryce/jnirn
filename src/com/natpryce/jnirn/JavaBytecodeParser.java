package com.natpryce.jnirn;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.google.common.collect.Maps.newTreeMap;

public class JavaBytecodeParser {
    private final SortedMap<String, ParsedClass> classesByName = newTreeMap();

    public Iterable<ParsedClass> classes() {
        return classesByName.values();
    }

    public void parse(File file) throws IOException {
        if (file.getName().endsWith(".jar")) {
            parseJAR(file);
        }
        else {
            throw new IOException("cannot parse " + file);
        }
    }

    public void parseJAR(File file) throws IOException {
        JarFile jarFile = new JarFile(file);

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                InputStream in = jarFile.getInputStream(jarEntry);
                try {
                    parseClassBytecode(file, in);
                } finally {
                    in.close();
                }
            }
        }
    }

    private void parseClassBytecode(File file, InputStream in) throws IOException {
        ClassReader classReader = new ClassReader(in);
        NativeMethodCollector collector = new NativeMethodCollector();
        classReader.accept(collector, ClassReader.SKIP_CODE);

        if (collector.foundNativeMethods()) {
            ParsedClass c = new ParsedClass(classReader.getClassName(), file, collector.nativeMethodsByName);
            classesByName.put(c.name, c);
        }
    }
}
