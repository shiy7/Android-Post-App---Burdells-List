package com.example.finalapp.model;

public class User {

    private String id;
    private String username;
    private String imageurl;
    private float rate;
    private String address;
    private String country;
    private String state;
    private String city;
    private String phone;
    private String email;
    private int reviewCount;

    public User(String id, String username, String imageurl, float rate, String address,
                String country, String state, String city, String phone, String email, int reviewCount) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.rate = rate;
        this.address = address;
        this.country = country;
        this.state = state;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.reviewCount = reviewCount;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}