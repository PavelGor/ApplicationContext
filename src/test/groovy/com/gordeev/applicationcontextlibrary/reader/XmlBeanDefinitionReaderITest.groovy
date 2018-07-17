package com.gordeev.applicationcontextlibrary.reader

import com.gordeev.applicationcontextlibrary.reader.xml.XmlBeanDefinitionReader
import spock.lang.Specification

class XmlBeanDefinitionReaderITest extends Specification {

    void testReadBeanDefinitions() {
        String[] paths = ["src/test/resources/xml/context.xml"]
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(paths)

        given: "given"
        def actualBeanDefinitions = reader.readBeanDefinitions()

        expect: "expect"
        actualBeanDefinitions.get(0).id == "userService"
        actualBeanDefinitions.get(0).beanClassName == "com.gordeev.applicationcontextlibrary.fortest.UserService"
        actualBeanDefinitions.get(0).refDependencies == ['mailService':'mailService']
        actualBeanDefinitions.get(0).dependencies == [:]

        actualBeanDefinitions.get(1).id == "paymentWithMaxService"
        actualBeanDefinitions.get(2).id == "paymentService"
        actualBeanDefinitions.get(3).id == "consolLogBeanPostProcessor"

    }

}
