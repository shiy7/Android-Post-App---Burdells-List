package com.example.finalapp.model;

public class Shop {

    private String postid;
    private int quantity;

    public Shop(String postid, int quantity) {
        this.postid = postid;
        this.quantity = quantity;
    }

    public Shop() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
