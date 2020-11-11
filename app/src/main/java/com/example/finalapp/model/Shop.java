package com.example.finalapp.model;

public class Shop {

    private String postid;
    private int quantity;
    private long totalPrice;

    public Shop(String postid, int quantity, long totalPrice) {
        this.postid = postid;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
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

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
