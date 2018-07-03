package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.beandefinitionreader.BeanDefinitionReader;

import java.util.List;

public interface ApplicationContext <T> {
    Object getBean(String name);
    T getBean(Class<T> tClass);
    T getBean(String name, Class<T> tClass);
    List<String> getBeanNames();
    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);
}
