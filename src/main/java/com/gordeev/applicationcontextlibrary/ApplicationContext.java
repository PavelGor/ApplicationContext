package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.reader.BeanDefinitionReader;

import java.util.List;
import java.util.Optional;

public interface ApplicationContext {
    Optional<Object> getBean(String name);
    <T> Optional<T> getBean(Class<T> clazz);
    <T> Optional<T> getBean(String name, Class<T> clazz);
    Optional<List<String>> getBeanNames();
    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);
}
