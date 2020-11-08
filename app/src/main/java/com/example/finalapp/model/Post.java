package com.example.finalapp.model;

import java.util.Date;
import java.util.List;

public class Post {
    private String poster;
    private String postid;
    private String title;
    private String type;
    private String category;
    private int amount;
    private double price;
    private String detail;
    private Date date;
    private List<String> images;
    private String status;
    private Long quantity;

    public Post(String poster, String postid, String title, String type, String category,
                int amount, double price, String detail, Date date, List<String> images, String status) {
        this.poster = poster;
        this.postid = postid;
        this.title = title;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.price = price;
        this.detail = detail;
        this.date = date;
        this.images = images;
        this.status = status;
    }

    public Post(String poster, String postid, String title, String type, String category, int amount, double price, String detail, Date date, List<String> images, String status, Long quantity) {
        this.poster = poster;
        this.postid = postid;
        this.title = title;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.price = price;
        this.detail = detail;
        this.date = date;
        this.images = images;
        this.status = status;
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Post() {

    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
