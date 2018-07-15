package com.gordeev.applicationcontextlibrary.injector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ValueInjector extends Injector {

    @Override
    public void injectValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException {
        Type[] types = method.getParameterTypes();
        Type type = types[0];
        if (type == int.class) {
            method.invoke(object, Integer.parseInt(value));
        } else if (type == String.class) {
            method.invoke(object, value);
        } else if (type == double.class) {
            method.invoke(object, Double.parseDouble(value));
        } else if (type == float.class) {
            method.invoke(object, Float.parseFloat(value));
        } else if (type == boolean.class) {
            method.invoke(object, Boolean.parseBoolean(value));
        } else if (type == short.class) {
            method.invoke(object, Short.parseShort(value));
        } else if (type == long.class) {
            method.invoke(object, Long.parseLong(value));
        }
    }
}
