package com.gordeev.applicationcontextlibrary;

import com.gordeev.applicationcontextlibrary.beandefinitionreader.BeanDefinitionReader;

import java.util.List;

public interface ApplicationContext {
    Object getBean(String name);
    Object getBean(Class tClass);
    Object getBean(String name, Class tClass);
    List<String> getBeanNames();
    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);
}
