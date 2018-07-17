package com.gordeev.applicationcontextlibrary.postprocessor;

public interface BeanPostProcessor {
    //run methods with annotation @PostProcessor

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);

}
