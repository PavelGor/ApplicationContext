package com.gordeev.applicationcontextlibrary.injector;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ValueInjector extends Injector {

    public ValueInjector(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {
        super(beanDefinitions, beanDefinitionBeanMap);
    }

    @Override
    protected Map<String, String> getDependencies(BeanDefinition beanDefinition) {
        return beanDefinition.getDependencies();
    }

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
