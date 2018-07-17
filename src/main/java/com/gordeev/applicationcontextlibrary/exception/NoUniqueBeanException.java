package com.gordeev.applicationcontextlibrary.exception;

public class NoUniqueBeanException extends RuntimeException{
    public NoUniqueBeanException() {
    }

    public NoUniqueBeanException(String message) {
        super(message);
    }

    public NoUniqueBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoUniqueBeanException(Throwable cause) {
        super(cause);
    }
}
