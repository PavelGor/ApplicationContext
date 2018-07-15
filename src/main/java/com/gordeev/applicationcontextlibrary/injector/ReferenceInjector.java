package com.gordeev.applicationcontextlibrary.injector;

import com.gordeev.applicationcontextlibrary.entity.Bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReferenceInjector extends Injector {
    private List<Bean> beans;

    public ReferenceInjector(List<Bean> beans) {
        super();
        this.beans = beans;
    }

    @Override
    public void injectValue(Object object, Method method, String value) throws InvocationTargetException, IllegalAccessException {
        for (Bean bean : beans) {
            if (bean.getId().equalsIgnoreCase(value)) {
                method.invoke(object, bean.getValue());
            }
        }

    }
}
