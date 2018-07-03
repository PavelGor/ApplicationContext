package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.beandefinitionreader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.beandefinitionreader.xml.XmlBeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext { //TODO: <T>
    private String[] paths;
    private BeanDefinitionReader reader;
    private List<Bean> beans = new ArrayList<>();
    private List<BeanDefinition> beanDefinitions;
    private final static ClassPathApplicationContext instance = new ClassPathApplicationContext();

    public ClassPathApplicationContext() {
    }

    public static ClassPathApplicationContext getInstance() {
        return instance;
    }

    public ClassPathApplicationContext(String[] paths) {
        //Default reader - XmlBeanDefinitionReader()
        if (reader == null) {
            setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
        }

        beanDefinitions = reader.readBeanDefinitions();
        createBeansFromBeanDefinitions();
        injectDependencies();
        injectRefDependencies();
    }

    public ClassPathApplicationContext(String path) {
        paths[0] = path;
        new ClassPathApplicationContext(paths);
    }

    @Override
    public Object getBean(String name) {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(name)){
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getBean(Class aClass) {
        for (Bean bean : beans) {
            if (bean.getValue().getClass() == aClass){
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getBean(String name, Class aClass) {//TODO: in what case it is possible?
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        List<String> names = new ArrayList<>();
        for (Bean bean : beans) {
            names.add(bean.getId());
        }
        return names;
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        reader = beanDefinitionReader;
    }

    private void createBeansFromBeanDefinitions(){
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = new Bean();
            String className = "";
            try {
                className = beanDefinition.getBeanClassName();
                bean.setValue(Class.forName(className).newInstance());
                bean.setId(beanDefinition.getId());
            } catch (InstantiationException | ClassNotFoundException |IllegalAccessException e) {
                e.printStackTrace(); //TODO: LOG
                throw new RuntimeException("Application have no such class: " + className + "\n" + e);
            }
            beans.add(bean);
        }
    }

    private void injectDependencies(){
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())){
                    setValues(bean.getValue(), beanDefinition.getDependencies());
                }
            }
        }
    }

    private void injectRefDependencies(){
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())){
                    setRefs(bean.getValue(), beanDefinition.getRefDependencies());
                }
            }
        }
    }

    private void setRefs(Object object, Map<String, String> dependencies) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String fieldName = method.getName().substring(3);
            if (dependencies.containsKey(fieldName)){
                try {
                    Object objectFromRef = Class.forName(dependencies.get(fieldName));
                    Object objectFromBeans = beans.get(beans.indexOf(objectFromRef));
                    method.invoke(objectFromBeans);
                } catch (IllegalAccessException | InvocationTargetException |ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Class " + object.getClass().getName() + " have no setter for field" + fieldName + e);
                }
            }
        }
    }

    private void setValues(Object object, Map<String, String> dependencies) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String fieldName = method.getName().substring(3);
            if (dependencies.containsKey(fieldName)){
//                try {
                    Object value = dependencies.get(fieldName);
                    method.getParameters();
                    Type[] types = method.getGenericParameterTypes();
                    Type type = types[0];
                    // check Type of variable

                    //method.invoke(value);
//                } catch (IllegalAccessException | InvocationTargetException e) {
//                    e.printStackTrace();
//                    throw new RuntimeException("Class " + object.getClass().getName() + " have no setter for field" + fieldName + e);
//                }
            }
        }
    }
}
