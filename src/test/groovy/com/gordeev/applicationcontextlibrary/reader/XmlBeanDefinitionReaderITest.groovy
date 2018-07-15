package com.gordeev.applicationcontextlibrary.reader

import com.gordeev.applicationcontextlibrary.reader.xml.XmlBeanDefinitionReader
import spock.lang.Specification

class XmlBeanDefinitionReaderITest extends Specification {

    void testReadBeanDefinitions() {
        String[] paths = ["src/test/groovy/com/gordeev/applicationcontextlibrary/fortest/context.xml"]
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
        actualBeanDefinitions.get(3).id == "mailService"

    }

    void testDeleteDoublesWithPaths(){
        given: "given"
        String[] paths = ["1.xml","2.xml","3.xml","4.xml","5.xml"]
        String[] importPaths = ["1.xml","6.xml","3.xml","7.xml","8.xml"]
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(paths)
        String[]  expectedPaths = xmlBeanDefinitionReader.deleteDoublesWithPaths(importPaths)

        expect:"arrays equals"
        expectedPaths == ["6.xml","7.xml","8.xml"]
        expectedPaths != ["1.xml","6.xml","3.xml","7.xml","8.xml"]
    }
}
