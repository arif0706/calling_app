package com.example;

public class Notification {

    public String title;
    public String body;
    public boolean delivery_receipt_requested;

    public Notification(){}

    public Notification(String title, String body){//,boolean delivery_receipt_requested) {
        this.title = title;
        this.body = body;
      //  this.delivery_receipt_requested=delivery_receipt_requested;
    }
}
