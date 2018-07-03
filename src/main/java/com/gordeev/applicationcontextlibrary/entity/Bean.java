package com.gordeev.applicationcontextlibrary.entity;

public class Bean {
    private Object value;
    private String id;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "value=" + value +
                ", id='" + id + '\'' +
                '}';
    }
}
