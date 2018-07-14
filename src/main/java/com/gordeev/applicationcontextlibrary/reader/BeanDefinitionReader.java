package com.gordeev.applicationcontextlibrary.reader;

import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();
}
