package com.example.finalapp.model;

import java.util.Date;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private Date date;
    private boolean seen;

    public Chat(String sender, String receiver, String message, Date date, boolean seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
        this.seen = seen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
