package com.gordeev.applicationcontextlibrary.fortest

import com.gordeev.applicationcontextlibrary.postprocessor.BeanPostProcessor

class ConsolLogBeanPostProcessor implements BeanPostProcessor{
    @Override
    Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("Bean with id " + beanName + " before init");
        return bean;
    }

    @Override
    Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("Bean with id " + beanName + " after init");
        return bean;
    }
}
