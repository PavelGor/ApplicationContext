package com.gordeev.applicationcontextlibrary.postprocessor;

import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;

import java.util.List;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList);
}
