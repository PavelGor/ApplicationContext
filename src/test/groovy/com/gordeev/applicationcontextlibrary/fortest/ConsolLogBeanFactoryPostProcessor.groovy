package com.gordeev.applicationcontextlibrary.fortest

import com.gordeev.applicationcontextlibrary.entity.BeanDefinition
import com.gordeev.applicationcontextlibrary.postprocessor.BeanFactoryPostProcessor

class ConsolLogBeanFactoryPostProcessor implements BeanFactoryPostProcessor{
    @Override
    void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList) {
        System.out.println("BeanFactoryPostProcessor doing something with beanDefinitionList");
    }
}
