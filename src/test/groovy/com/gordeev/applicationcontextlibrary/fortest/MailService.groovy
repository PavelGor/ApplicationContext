package com.gordeev.applicationcontextlibrary.fortest

import javax.annotation.PostConstruct;

public class MailService {
    private String protocol;
    private int port;

    @PostConstruct
    public void customMethod(){
        System.out.println("customMetod() with annotation run");
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
