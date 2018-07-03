package com.gordeev.applicationcontextlibrary.beandefinitionreader

import com.gordeev.applicationcontextlibrary.beandefinitionreader.xml.XmlBeanDefinitionReader
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition
import org.junit.Test
import spock.lang.Specification

class XmlBeanDefinitionReaderTest extends Specification {
    void testReadBeanDefinitions() {
        String[] paths = ["context.xml"]
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(paths)

        given: "given"
        def actualBeanDefinitions = reader.readBeanDefinitions()

        expect: "expect"
        actualBeanDefinitions.get(0).id == "userService"
        actualBeanDefinitions.get(0).beanClassName == "com.gordeev.applicationcontextlibrary.fortest.UserService"

        actualBeanDefinitions.get(1).id == "paymentWithMaxService"
        actualBeanDefinitions.get(2).id == "paymentService"
        actualBeanDefinitions.get(3).id == "mailService"

    }

}
