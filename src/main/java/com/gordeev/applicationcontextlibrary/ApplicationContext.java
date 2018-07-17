package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.reader.BeanDefinitionReader;

import java.util.List;
import java.util.Optional;

public interface ApplicationContext {
    Object getBean(String name);
    <T> T getBean(Class<T> clazz);
    <T> T getBean(String name, Class<T> clazz);
    List<String> getBeanNames();
    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);
}
