package com.example.journalapplication;

import com.google.firebase.Timestamp;

public class Journal {
    private String title;
    private String imageUrl;
    private String thoughts;
    private String userId;
    private com.google.firebase.Timestamp time;
    private String userName;

    public Journal(String title, String imageUrl, String thoughts, String userId, Timestamp time, String userName) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.thoughts = thoughts;
        this.userId = userId;
        this.time = time;
        this.userName = userName;
    }

    public Journal() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public com.google.firebase.Timestamp getTime() {
        return time;
    }

    public void setTime(com.google.firebase.Timestamp time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
