package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import com.gordeev.applicationcontextlibrary.exception.BeanInstantiationException;
import com.gordeev.applicationcontextlibrary.exception.NoUniqueBeanException;
import com.gordeev.applicationcontextlibrary.injector.Injector;
import com.gordeev.applicationcontextlibrary.injector.ReferenceInjector;
import com.gordeev.applicationcontextlibrary.injector.ValueInjector;
import com.gordeev.applicationcontextlibrary.postprocessor.BeanFactoryPostProcessor;
import com.gordeev.applicationcontextlibrary.postprocessor.BeanPostProcessor;
import com.gordeev.applicationcontextlibrary.reader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.reader.xml.XmlBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathApplicationContext.class);
    private BeanDefinitionReader reader;
    private List<Bean> beans = new ArrayList<>();
    private List<Bean> postProcessingBeans = new ArrayList<>();

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
            if (clazz.isAssignableFrom(bean.getValue().getClass())) { //clazz.isAssignableFrom(bean.getValue().getClass()) //bean.getValue().getClass() == clazz
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

        //Post process for beanDefinitions
        runBeanFactoryPostProcessing(beanDefinitions);

        createBeansFromBeanDefinitions(beanDefinitions, beanDefinitionBeanMap);
        LOG.info("Beans were set: {}  thing(s)", beans.size());

        injectDependencies(beanDefinitions, beanDefinitionBeanMap);
        LOG.info("Dependencies and Variables were set for objects");

        //Post process for beans
        runBeanPostProcessing(postProcessingBeans);

    }

    private void runBeanFactoryPostProcessing(List<BeanDefinition> beanDefinitions) {
        try {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
                    Object objectBeanFactoryPostProcessor = clazz.newInstance();
                    Method method = clazz.getMethod("postProcessBeanFactory", List.class);
                    method.invoke(objectBeanFactoryPostProcessor, beanDefinitions);
                    Bean bean = new Bean();
                    bean.setValue(objectBeanFactoryPostProcessor);
                    bean.setId(beanDefinition.getId());
                    postProcessingBeans.add(bean);
                }
            }
        } catch (Exception e) {
            LOG.error("BeanFactoryPostProcessing failed", e);
            throw new BeanInstantiationException("BeanFactoryPostProcessing failed", e);
        }

    }

    private void createBeansFromBeanDefinitions(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {

        Iterator beanDefinitionIterator = beanDefinitions.iterator();
        while (beanDefinitionIterator.hasNext()) {
            BeanDefinition beanDefinition = (BeanDefinition) beanDefinitionIterator.next();
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (beanDefinition.getId() != null && !BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    Object newObject = clazz.newInstance();

                    Bean bean = new Bean();
                    bean.setValue(newObject);
                    bean.setId(beanDefinition.getId());

                    beans.add(bean);
                    beanDefinitionBeanMap.put(beanDefinition, bean);
                } else {
                    beanDefinitionIterator.remove();
                }
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                LOG.error("Cannot create such class: {}", className, e);
                throw new BeanInstantiationException("Cannot create such class: " + className + "\n", e);
            }
        }
    }

    private void runBeanPostProcessing(List<Bean> beans) {

        List<Bean> postProcessingBeans = scanBeansOnPostProcessing(beans);

        try {
            runPostProcessingMethod("postProcessBeforeInitialization", postProcessingBeans);

            initOnAnnotations(beans);

            runPostProcessingMethod("postProcessAfterInitialization", postProcessingBeans);

        } catch (Exception e) {
            LOG.error("BeanFactoryPostProcessing failed", e);
            throw new BeanInstantiationException("BeanFactoryPostProcessing failed", e);
        }

    }

    private List<Bean> scanBeansOnPostProcessing(List<Bean> beans) {
        List<Bean> postProcessingBeans = new ArrayList<>();
        for (Bean bean : beans) {
            if (BeanPostProcessor.class.isAssignableFrom(bean.getValue().getClass())) {
                postProcessingBeans.add(bean);
            }
        }
        LOG.info("found : {} bean(s) for postProcessing: {}", postProcessingBeans.size(), postProcessingBeans.toString());
        return postProcessingBeans;
    }

    private void runPostProcessingMethod(String methodName, List<Bean> postProcessingBeans) throws Exception {
        for (Bean postProcessingBean : postProcessingBeans) {
            for (Bean bean : beans) {
                Method method = postProcessingBean.getValue().getClass().getMethod(methodName, Object.class, String.class);
                Object newBeanValue = method.invoke(postProcessingBean.getValue(), bean.getValue(), bean.getId());
                bean.setValue(newBeanValue);
                LOG.info("runPostProcessingMethod: {} for bean: {}", methodName, bean.getId());
            }
        }
    }

    private void initOnAnnotations(List<Bean> beans) throws Exception {
        for (Bean bean : beans) {
            Method[] methods = bean.getValue().getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.invoke(bean.getValue());
                    LOG.info("found bean {} with annotated method: {} ", bean.getId(), method.getName());
                }
            }
        }
    }

    private void injectDependencies(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {

        for (Injector injector : new Injector[]{new ReferenceInjector(beanDefinitions, beanDefinitionBeanMap), new ValueInjector(beanDefinitions, beanDefinitionBeanMap)}) {
            injector.inject();
        }

    }
}
