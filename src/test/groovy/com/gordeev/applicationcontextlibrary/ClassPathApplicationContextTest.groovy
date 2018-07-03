package com.gordeev.applicationcontextlibrary

import com.gordeev.applicationcontextlibrary.fortest.MailService
import spock.lang.Specification

class ClassPathApplicationContextTest extends Specification {
    String[] paths = ["context.xml"]
    ApplicationContext applicationContext = new ClassPathApplicationContext(paths)

    void testGetBean() {
        expect: "expect"
        applicationContext.getBean("userService").getClass().getSimpleName() == "UserService"
        applicationContext.getBean(MailService.class).getClass().getSimpleName() == "MailService"
    }

    void testGetBeanNames() {
        given:
        def classNameList = applicationContext.getBeanNames()

        expect:"lists equals"
        classNameList == ['userService', 'paymentWithMaxService', 'paymentService', 'mailService']
    }

}
