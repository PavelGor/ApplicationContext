package com.gordeev.applicationcontextlibrary

import com.gordeev.applicationcontextlibrary.fortest.MailService
import com.gordeev.applicationcontextlibrary.fortest.PaymentWithMaxService
import com.gordeev.applicationcontextlibrary.fortest.UserService
import spock.lang.Specification

class ClassPathApplicationContextITest extends Specification {
    String[] paths = ["src/test/groovy/com/gordeev/applicationcontextlibrary/fortest/context.xml"]
    ApplicationContext applicationContext = new ClassPathApplicationContext(paths)

    void testGetBean() {
        given:
        Optional<PaymentWithMaxService> optionalPaymentWithMaxService = applicationContext.getBean(PaymentWithMaxService.class)
        PaymentWithMaxService paymentWithMaxService
        if (optionalPaymentWithMaxService.isPresent()){
            paymentWithMaxService = optionalPaymentWithMaxService.get()
        }

        Class<UserService> clazz = UserService.class
        Optional<UserService> optionalUserService = applicationContext.getBean(clazz)
        UserService userService
        if (optionalUserService.isPresent()){
            userService = optionalUserService.get()
        }

        expect: "expect"
        userService.getMailService().class == MailService.class
        applicationContext.getBean("userService").get().getClass().getSimpleName() == "UserService"
        applicationContext.getBean(MailService.class).get().getClass().getSimpleName() == "MailService"
        applicationContext.getBean("userService", UserService.class).get().getClass().getSimpleName() == "UserService"

        paymentWithMaxService.maxAmount == 5000
        paymentWithMaxService.mailService == (applicationContext.getBean(MailService.class).get())

    }

    void testGetBeanNames() {
        given:
        def classNameList = applicationContext.getBeanNames().get()

        expect:"equals"
        classNameList == ['userService', 'paymentWithMaxService', 'paymentService', 'mailService']
    }

//    void testExceptionExpected(){
//            setup:
//            String[] paths = ["context_error.xml"]
//            ApplicationContext applicationContext = new ClassPathApplicationContext(paths)
//
//            when:
//            applicationContext.getBean("userService")
//
//            then:
//            final RuntimeException exception = thrown()
//            exception.message == 'Application have no such class: com.gordeev.applicationcontextlibrary.fortest.User'
//    }


}
