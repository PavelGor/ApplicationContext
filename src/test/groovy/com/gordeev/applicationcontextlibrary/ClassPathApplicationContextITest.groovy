package com.gordeev.applicationcontextlibrary

import com.gordeev.applicationcontextlibrary.exception.BeanInstantiationException
import com.gordeev.applicationcontextlibrary.fortest.MailService
import com.gordeev.applicationcontextlibrary.fortest.PaymentWithMaxService
import com.gordeev.applicationcontextlibrary.fortest.UserService
import spock.lang.Specification

class ClassPathApplicationContextITest extends Specification {
    String[] paths = ["src/test/resources/xml/context.xml"]
    ApplicationContext applicationContext = new ClassPathApplicationContext(paths)

    void testGetBean() {
        given:
        PaymentWithMaxService paymentWithMaxService = applicationContext.getBean(PaymentWithMaxService.class)

        UserService userService = applicationContext.getBean(UserService.class)

        expect: "expect"
        userService.getMailService().class == MailService.class
        applicationContext.getBean("userService").getClass().getSimpleName() == "UserService"
        applicationContext.getBean(MailService.class).getClass().getSimpleName() == "MailService"
        applicationContext.getBean("userService", UserService.class).getClass().getSimpleName() == "UserService"

        paymentWithMaxService.maxAmount == 5000
        paymentWithMaxService.mailService == (applicationContext.getBean(MailService.class))

    }

    void testGetBeanNames() {
        given:
        def classNameList = applicationContext.getBeanNames()

        expect:"equals"
        classNameList == ['userService', 'paymentWithMaxService', 'paymentService', 'mailService']
    }

}
