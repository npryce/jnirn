package com.natpryce.jnirn.proguard;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class ClassMethodMapper {
    private final Map<String, MapFileParser.MangledClass> parsed;

    public ClassMethodMapper(Map<String, MapFileParser.MangledClass> parsed) {
        this.parsed = parsed;
    }

    public String mapClass(String className) {
        if (parsed.containsKey(className)) {
            return parsed.get(className).mangledClassName;
        }
        throw new IllegalArgumentException("Unknown class " + className);
    }

    public ClassMethod map(ClassMethod classMethod) {
        Method method = classMethod.method;

        return new ClassMethod(
                mapClass(classMethod.className),
                new Method(
                        parsed.get(classMethod.className).methods.get(method.toString()).mangledName,
                        mapType(method.getReturnType()),
                        newArrayList(Iterables.transform(newArrayList(method.getArgumentTypes()), mapType())).toArray(new Type[0]))
        );
    }

    private Function<Type, Type> mapType() {
        return new Function<Type, Type>() {
            @Override
            public Type apply(Type input) {
                return mapType(input);
            }
        };
    }

    private Type mapType(Type type) {
        int sort = type.getSort();
        if (sort == Type.ARRAY) {

            Type arrayType = type.getElementType();
            int arrayDimension = type.getDimensions();

            Type mappedType = mapType(arrayType);

            return Type.getType(Strings.repeat("[", arrayDimension) + mappedType);

        } else if (sort == Type.OBJECT) {
            String internalName = type.getInternalName();
            if (parsed.containsKey(internalName)) {
                return Type.getObjectType(parsed.get(internalName).mangledClassName);
            }
            return type;
        } else {
            return type;
        }
    }
}
