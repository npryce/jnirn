package com.natpryce.jnirn.proguard;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class MethodMaker {

    private final JavaTypeConverter converter = new JavaTypeConverter();
    private final Function<String, Type> toType = new Function<String, Type>() {
        @Override
        public Type apply(String input) {
            return converter.type(input);
        }
    };

    public Method descriptorFor(String name, String returnType, List<String> parameters) {
        return new Method(
                name,
                converter.type(returnType),
                newArrayList(Iterables.transform(parameters, toType)).toArray(new Type[0]));
    }
}
