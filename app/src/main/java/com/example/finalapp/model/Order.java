package com.example.finalapp.model;

import java.util.Date;

public class Order {

    private String id;
    private String postid;
    private String buyer;
    private String seller;
    private String buyerStatus;
    private String sellerStatus;
    private Date date;
    private int amount;

    public Order(String id, String postid, String buyer, String seller, String buyerStatus, String sellerStatus, Date date, int amount) {
        this.id = id;
        this.postid = postid;
        this.buyer = buyer;
        this.seller = seller;
        this.buyerStatus = buyerStatus;
        this.sellerStatus = sellerStatus;
        this.date = date;
        this.amount = amount;
    }

    public Order() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyerStatus() {
        return buyerStatus;
    }

    public void setBuyerStatus(String buyerStatus) {
        this.buyerStatus = buyerStatus;
    }

    public String getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(String sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}