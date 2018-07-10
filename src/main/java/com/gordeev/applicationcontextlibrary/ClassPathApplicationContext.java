package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.beandefinitionreader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.beandefinitionreader.xml.XmlBeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathApplicationContext.class);
    private static final ClassPathApplicationContext instance = new ClassPathApplicationContext();
    private String[] paths;
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;
    private List<Bean> beans = new ArrayList<>();

    public ClassPathApplicationContext() {
    }

    public static ClassPathApplicationContext getInstance() {
        return instance;
    }

    public ClassPathApplicationContext(String[] paths) {
        this.paths = paths;
        setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
    }

    public ClassPathApplicationContext(String path) {
        paths[0] = path;
        setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
    }

    @Override
    public Object getBean(String name) {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(name)) {
                LOG.info("User get bean with name: {}", name);
                return bean.getValue();
            }
        }
        return null;//TODO: think about other way (not null) - change after guava Labs
    }

    @Override
    public Object getBean(Class aClass) {
        for (Bean bean : beans) {
            if (bean.getValue().getClass() == aClass) {
                LOG.info("User get bean with name: {}", aClass.getName());
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getBean(String name, Class aClass) {// in what case it is possible?
        for (Bean bean : beans) {
            if (bean.getValue().getClass() == aClass && bean.getId().equalsIgnoreCase(name)) {
                LOG.info("User get bean with name: {}", name);
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        List<String> names = new ArrayList<>();
        for (Bean bean : beans) {
            names.add(bean.getId());
        }
        LOG.info("User get List of beans: {}", names);
        return names;
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
            String className = "";
            try {
                className = beanDefinition.getBeanClassName();
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
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())) {
                    setObjectVariablesValue(bean.getValue(), beanDefinition.getDependencies(), false);//Is there other algorithm to organize code without boolean?
                    setObjectVariablesValue(bean.getValue(), beanDefinition.getRefDependencies(), true);
                }
            }
        }
    }

    private void setObjectVariablesValue(Object object, Map<String, String> dependencies, boolean isReference) {
        String objectName = object.getClass().getName();
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Map.Entry<String, String> dependency : dependencies.entrySet()) {
            boolean isVariableSet = false;
            for (Method method : methods) {
                String fieldName = method.getName().substring(3);
                String dependencyName = dependency.getKey();
                if (dependencyName.equalsIgnoreCase(fieldName) && method.getName().contains("set")) {
                    try {
                        if (isReference){
                            method.invoke(object, getBean(dependency.getValue()));
                        } else {
                            injectVariableValue(object, method, dependency.getValue());
                        }
                        isVariableSet = true;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        LOG.error("Object {} have no setter for field: {}", objectName, dependencyName);
                        throw new RuntimeException("Class " + objectName + " have no setter for field: " + dependencyName + "\n" + e);
                    }
                }
            }
            if (!isVariableSet) {
                LOG.error("Object {} have no setter for field: {}", objectName, dependency.getKey());
                throw new RuntimeException("Class " + objectName + " have no setter for field: " + dependency.getKey());
            }
        }
    }

    private void injectVariableValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException {
        method.getParameters();
        Type[] types = method.getParameterTypes();
        Type type = types[0];
        if (type == int.class){
            method.invoke(object, Integer.parseInt(value));
        } else if (type == String.class){
            method.invoke(object, value);
        } else if (type == double.class){
            method.invoke(object, Double.parseDouble(value));
        } else if (type == float.class){
            method.invoke(object, Float.parseFloat(value));
        } else if (type == boolean.class){
            method.invoke(object, Boolean.parseBoolean(value));
        } else if (type == short.class){
            method.invoke(object, Short.parseShort(value));
        } else if (type == long.class){
            method.invoke(object, Long.parseLong(value));
        }
    }
}
