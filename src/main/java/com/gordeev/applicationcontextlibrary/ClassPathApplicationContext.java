package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import com.gordeev.applicationcontextlibrary.reader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.reader.xml.XmlBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathApplicationContext.class);
    private String[] paths;
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;
    private List<Bean> beans = new ArrayList<>();

    public ClassPathApplicationContext(String[] paths) {
        this.paths = paths;
        setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
    }

    public ClassPathApplicationContext(String path) {
        paths[0] = path;
        setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
    }

    @Override
    public Optional<Object> getBean(String name) {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(name)) {
                LOG.info("User get bean with name: {}", name);
                return Optional.of(bean.getValue());
            }
        }
        return java.util.Optional.empty();
    }

    @Override
    public <T> Optional<T> getBean(Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getValue().getClass() == clazz) {
                LOG.info("User get bean with name: {}", clazz.getName());
                return Optional.of(clazz.cast(bean.getValue()));
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getBean(String name, Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(name)) {
                LOG.info("User get bean with name: {}", name);
                return Optional.of(clazz.cast(bean.getValue()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> getBeanNames() {
        List<String> names = new ArrayList<>();
        if (beans != null) {
            for (Bean bean : beans) {
                names.add(bean.getId());
            }
            LOG.info("User get List of beans: {}", names);
            return Optional.of(names);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        reader = beanDefinitionReader;
        createContext();
    }

    private void createContext() {
        beanDefinitions = reader.readBeanDefinitions();
        LOG.info("Definitions were got: {} thing(s)", beanDefinitions.size());
        createBeansFromBeanDefinitions();
        LOG.info("Beans were set: {}  thing(s)", beans.size());
        injectDependencies();
        LOG.info("Dependencies and Variables were set for objects");
    }

    private void createBeansFromBeanDefinitions() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = new Bean();
            String className = beanDefinition.getBeanClassName();
            try {
                Object newObject = Class.forName(className).newInstance();
                bean.setValue(newObject);
                bean.setId(beanDefinition.getId());
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                LOG.error("Application have no such class: {}", className);
                throw new RuntimeException("Application have no such class: " + className + "\n" + e);
            }
            beans.add(bean);
        }
    }

    private void injectDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Optional<Object> optionalBeanValue = getBean(beanDefinition.getId());
            Map<String, String> dependencies = beanDefinition.getDependencies();
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();

            if (optionalBeanValue.isPresent()) {
                Object beanValue = optionalBeanValue.get();
                if (refDependencies != null) {
                    injectObjectVariablesValues(beanValue, refDependencies, true);
                }
                if (dependencies != null) {
                    injectObjectVariablesValues(beanValue, dependencies, false);
                }
            }
        }
    }

    private void injectObjectVariablesValues(Object object, Map<String, String> dependencies, boolean isReference) {
        String objectName = object.getClass().getName();
        Method[] methods = object.getClass().getDeclaredMethods();

        for (Map.Entry<String, String> dependency : dependencies.entrySet()) {
            boolean isDependencySet = false;
            String dependencyName = dependency.getKey();
            for (Method method : methods) {

                if (method.getName().startsWith("set")) {
                    String fieldName = method.getName().substring(3);
                    if (fieldName.equalsIgnoreCase(dependencyName)) {
                        try {
                            if (!isReference) {

                                injectVariableValue(object, method, dependency.getValue());

                            } else if (getBean(dependency.getValue()).isPresent()){

                                method.invoke(object, getBean(dependency.getValue()).get());

                            }
                            isDependencySet = true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            LOG.error("Object {} have no setter for field: {}", objectName, dependencyName);
                            throw new RuntimeException("Class " + objectName + " have no setter for field: " + dependencyName + "\n" + e);
                        }
                    }
                }
            }
            if (!isDependencySet) {
                LOG.error("Object {} have no setter for field: {}", objectName, dependency.getKey());
                throw new RuntimeException("Class " + objectName + " have no setter for field: " + dependency.getKey());
            }
        }
    }

    private void injectVariableValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException {
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
