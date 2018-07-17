package com.gordeev.applicationcontextlibrary.injector;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import com.gordeev.applicationcontextlibrary.exception.BeanInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class Injector {
    private static final Logger LOG = LoggerFactory.getLogger(Injector.class);
    private List<BeanDefinition> beanDefinitions;
    private Map<BeanDefinition, Bean> beanDefinitionBeanMap;

    Injector(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {
        this.beanDefinitions = beanDefinitions;
        this.beanDefinitionBeanMap = beanDefinitionBeanMap;
    }

    public void inject() {

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Object object = beanDefinitionBeanMap.get(beanDefinition).getValue();
            String objectName = object.getClass().getName();

            Method[] methods = object.getClass().getDeclaredMethods();
            Map<String, String> dependencies = getDependencies(beanDefinition);

            for (Map.Entry<String, String> dependency : dependencies.entrySet()) {
                String dependencyName = dependency.getKey();
                String dependencyValue = dependency.getValue();

                Method method = getMethod(dependency, methods);

                try {
                    injectValue(object, method, dependencyValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error("Object {} have no setter for field: {}", objectName, dependencyName);
                    throw new BeanInstantiationException("Class " + objectName + " have no setter for field: " + dependencyName, e);
                }
            }

        }

    }

    private Method getMethod(Map.Entry<String, String> dependency, Method[] methods) {
        String dependencyName = dependency.getKey();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                String fieldName = method.getName().substring(3);
                if (fieldName.equalsIgnoreCase(dependencyName)) {
                    return method;
                }
            }
        }
        return null;
    }

    protected abstract Map<String,String> getDependencies(BeanDefinition beanDefinition);


    protected abstract void injectValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException;
}
