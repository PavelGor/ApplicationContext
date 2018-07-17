package com.gordeev.applicationcontextlibrary.fortest;

public class PaymentWithMaxService {
    private int maxAmount;
    private Object mailService;

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Object getMailService() {
        return mailService;
    }

    public void setMailService(Object mailService) {
        this.mailService = mailService;
    }
}
