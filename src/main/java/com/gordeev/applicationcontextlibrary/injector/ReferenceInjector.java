package com.gordeev.applicationcontextlibrary.injector;

import com.gordeev.applicationcontextlibrary.entity.Bean;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ReferenceInjector extends Injector {
    private Map<BeanDefinition, Bean> beanDefinitionBeanMap;

    public ReferenceInjector(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionBeanMap) {
        super(beanDefinitions, beanDefinitionBeanMap);
        this.beanDefinitionBeanMap = beanDefinitionBeanMap;
    }

    @Override
    protected Map<String, String> getDependencies(BeanDefinition beanDefinition) {
        return beanDefinition.getRefDependencies();
    }

    @Override
    public void injectValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException {
        for (Map.Entry<BeanDefinition, Bean> beanDefinitionBeanEntry : beanDefinitionBeanMap.entrySet()) {
            Bean bean = beanDefinitionBeanEntry.getValue();
            if (bean.getId().equalsIgnoreCase(value)) {
                method.invoke(object, bean.getValue());
            }
        }
    }
}
