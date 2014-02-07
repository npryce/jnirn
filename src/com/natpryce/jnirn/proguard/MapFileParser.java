package com.natpryce.jnirn.proguard;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import org.objectweb.asm.commons.Method;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class MapFileParser {
    private static final Pattern CLASS_PATTERN = Pattern.compile("^(\\S+) -> (\\S+):");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^\\s+(.*?) (\\w+)\\((.*?)\\) -> (\\w+)");

    private static final MethodMaker methodMaker = new MethodMaker();

    public static class MangledClass {
        public final String realClassName;
        public final String mangledClassName;
        public final ImmutableMap<String, MangledMethod> methods;

        public MangledClass(String realClassName, String mangledClassName, ImmutableMap<String, MangledMethod> methods) {
            this.realClassName = realClassName;
            this.mangledClassName = mangledClassName;
            this.methods = methods;
        }
    }

    public static class MangledMethod {
        public final Method method;
        public final String mangledName;

        public MangledMethod(Method method, String mangledName) {
            this.method = method;
            this.mangledName = mangledName;
        }
    }

    public Map<String, MangledClass> parse(Iterable<String> lines) throws IOException {
        Map<String,MangledClass> classMap = newHashMap();

        String mangledClassName = null;
        String realClassName = null;
        Map<String,MangledMethod> methodMap = newHashMap();

        for (String line : lines) {
            Matcher classMatcher = CLASS_PATTERN.matcher(line);

            if (classMatcher.find()) {
                if (mangledClassName != null ) {
                    classMap.put(realClassName, new MangledClass(realClassName, mangledClassName, ImmutableMap.copyOf(methodMap)));
                }

                mangledClassName = toBinaryName(classMatcher.group(2));
                realClassName = toBinaryName(classMatcher.group(1));
                methodMap = newHashMap();

                continue;
            }

            if (mangledClassName == null) {
                continue;
            }

            Matcher methodMatcher = METHOD_PATTERN.matcher(line);
            if (methodMatcher.find()) {
                String returnType = methodMatcher.group(1);
                String methodName = methodMatcher.group(2);
                String parameterTypes = methodMatcher.group(3);
                String mangledName = methodMatcher.group(4);

                Method method = methodMaker.descriptorFor(methodName, returnType, newArrayList(Splitter.on(",").omitEmptyStrings().split(parameterTypes)));

                methodMap.put(method.toString(), new MangledMethod(method, mangledName));
            }
        }

        if (mangledClassName != null) {
            classMap.put(mangledClassName, new MangledClass(realClassName, mangledClassName, ImmutableMap.copyOf(methodMap)));
        }

        return classMap;
    }

    private String toBinaryName(String className) {
        return className.replace(".", "/");
    }
}
