package com.natpryce.jnirn;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaBytecodeParser {
    private final SortedMap<String, Map<String,List<NativeMethod>>> nativeMethodsByClass = new TreeMap<String, Map<String, List<NativeMethod>>>();

    public SortedMap<String, Map<String,List<NativeMethod>>> nativeMethodsByClass() {
        return nativeMethodsByClass;
    }

    public void parseJAR(File file) throws IOException {
        JarFile jarFile = new JarFile(file);

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                InputStream in = jarFile.getInputStream(jarEntry);
                try {
                    parseClassBytecode(in);
                } finally {
                    in.close();
                }
            }
        }
    }

    private void parseClassBytecode(InputStream in) throws IOException {
        ClassReader classReader = new ClassReader(in);
        NativeMethodCollector collector = new NativeMethodCollector();
        classReader.accept(collector, ClassReader.SKIP_CODE);

        if (collector.foundNativeMethods()) {
            nativeMethodsByClass.put(classReader.getClassName(), collector.nativeMethodsByName);
        }
    }
}
