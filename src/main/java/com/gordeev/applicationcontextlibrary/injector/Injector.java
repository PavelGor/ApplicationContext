package com.gordeev.applicationcontextlibrary.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class Injector {
    private static final Logger LOG = LoggerFactory.getLogger(Injector.class);

    public void injectDependencies(Object object, Map<String, String> dependencies) {
        String objectName = object.getClass().getName();
        Method[] methods = object.getClass().getDeclaredMethods();

        for (Map.Entry<String, String> dependency : dependencies.entrySet()) {
            boolean isDependencySet = false;
            String dependencyName = dependency.getKey();
            String dependencyValue = dependency.getValue();

            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    String fieldName = method.getName().substring(3);
                    if (fieldName.equalsIgnoreCase(dependencyName)) {
                        try {
                            injectValue(object, method, dependencyValue);
                            isDependencySet = true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            LOG.error("Object {} have no setter for field: {}", objectName, dependencyName);
                            throw new RuntimeException("Class " + objectName + " have no setter for field: " + dependencyName + "\n" + e);
                        }
                    }
                }
            }
            if (!isDependencySet) {
                LOG.error("Object {} have no setter for field: {}", objectName, dependencyName);
                throw new RuntimeException("Class " + objectName + " have no setter for field: " + dependencyName);
            }
        }
    }

    public abstract void injectValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException;
}
