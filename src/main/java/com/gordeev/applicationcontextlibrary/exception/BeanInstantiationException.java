package com.gordeev.applicationcontextlibrary.exception;

public class BeanInstantiationException extends RuntimeException{
    public BeanInstantiationException(String message) {
        super(message);
    }

    public BeanInstantiationException() {
    }

    public BeanInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanInstantiationException(Throwable cause) {
        super(cause);
    }
}
