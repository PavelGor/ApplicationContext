package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
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
    private String[] paths;
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;
    private List<Bean> beans = new ArrayList<>();
    private Map<BeanDefinition, Bean> beanDefinitionBeanMap = new HashMap<>();

    public ClassPathApplicationContext(String... paths) {
        this.paths = paths;
        setBeanDefinitionReader(new XmlBeanDefinitionReader(paths));
    }

    public ClassPathApplicationContext(String path) {
        paths = new String[]{path};
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
            beanDefinitionBeanMap.put(beanDefinition, bean);
        }
    }

    private void injectDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Object beanObject = beanDefinitionBeanMap.get(beanDefinition).getValue();
            Map<String, String> dependencies = beanDefinition.getDependencies();
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();

            if (refDependencies != null) {
                Injector injector = new ReferenceInjector(beans);
                injector.injectDependencies(beanObject, refDependencies);
            }
            if (dependencies != null) {
                Injector injector = new ValueInjector();
                injector.injectDependencies(beanObject, dependencies);
            }
        }
    }

}
