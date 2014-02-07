package com.natpryce.jnirn.proguard;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JavaTypeConverter {

    private Splitter dimension = Splitter.on("[]");

    public Type type(String javaTypeOrClassName) {

        Iterable<String> split = dimension.split(javaTypeOrClassName);

        Iterator<String> iterator = split.iterator();
        String typename = iterator.next();

        int dimension = Iterators.size(iterator);

        return Type.getType(Strings.repeat("[", dimension) + convertTypeByHand(typename));
    }

    private String convertTypeByHand(String methodParameterType) {

        if ( builtInMap.containsKey(methodParameterType)) {
            return builtInMap.get(methodParameterType);
        }

        return "L" + methodParameterType.replaceAll("\\.", "/") + ";";
    }

    private Map<String,String> builtInMap = new HashMap<String,String>(){{
        put("int", "I" );
        put("long", "J" );
        put("double", "D" );
        put("float", "F" );
        put("boolean", "Z" );
        put("char", "C" );
        put("byte", "B" );
        put("void", "V" );
        put("short", "S" );
    }};
}
