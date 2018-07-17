package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import com.gordeev.applicationcontextlibrary.exception.BeanInstantiationException;
import com.gordeev.applicationcontextlibrary.exception.NoUniqueBeanException;
import com.gordeev.applicationcontextlibrary.injector.Injector;
import com.gordeev.applicationcontextlibrary.injector.ReferenceInjector;
import com.gordeev.applicationcontextlibrary.injector.ValueInjector;
import com.gordeev.applicationcontextlibrary.reader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.reader.xml.XmlBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathApplicationContext.class);
    private BeanDefinitionReader reader;
    private List<Bean> beans = new ArrayList<>();

    public ClassPathApplicationContext() {
    }

    public ClassPathApplicationContext(String path) {
        this(new String[]{path});
    }

    public ClassPathApplicationContext(String... paths) {
        setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
        createContext();
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        reader = beanDefinitionReader;
    }


    @Override
    public Object getBean(String name) {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(name)) {
                LOG.info("Found bean with name: {}", name);
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        List<Bean> foundBeanList = new ArrayList<>();
        for (Bean bean : beans) {
            if (bean.getValue().getClass() == clazz) {
                foundBeanList.add(bean);
            }
        }
        int size = foundBeanList.size();
        if (size == 1) {
            LOG.info("Found bean with name: {}", clazz.getName());
            return clazz.cast(foundBeanList.get(0).getValue());
        } else if (size == 0) {
            return null;
        } else {
            LOG.error("There are {} objects with className: {}", size, clazz.getName());
            throw new NoUniqueBeanException("There are " + size + " objects with className: " + clazz.getName());
        }
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(name)) {
                LOG.info("Found bean with name: {}", name);
                return clazz.cast(bean.getValue());
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
        LOG.info("Found list of beans: {}", names);
        return names;
    }

    public void createContext() {
        Map<BeanDefinition, Bean> beanDefinitionBeanMap = new HashMap<>();

        List<BeanDefinition> beanDefinitions = reader.readBeanDefinitions();
        LOG.info("Definitions were found: {} thing(s)", beanDefinitions.size());

        createBeansFromBeanDefinitions(beanDefinitions, beanDefinitionBeanMap);
        LOG.info("Beans were set: {}  thing(s)", beans.size());

        injectDependencies(beanDefinitions, beanDefinitionBeanMap);
        LOG.info("Dependencies and Variables were set for objects");
    }

    private void createBeansFromBeanDefinitions(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = new Bean();
            String className = beanDefinition.getBeanClassName();
            try {
                Object newObject = Class.forName(className).newInstance();
                bean.setValue(newObject);
                bean.setId(beanDefinition.getId());
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                LOG.error("Cannot create such class: {}", className, e);
                throw new BeanInstantiationException("Cannot create such class: " + className + "\n", e);
            }
            beans.add(bean);
            beanDefinitionBeanMap.put(beanDefinition, bean);
        }
    }

    private void injectDependencies(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {

        for (Injector injector : new Injector[]{new ReferenceInjector(beanDefinitions, beanDefinitionBeanMap), new ValueInjector(beanDefinitions, beanDefinitionBeanMap)}){
            injector.inject();
        }

    }
}
