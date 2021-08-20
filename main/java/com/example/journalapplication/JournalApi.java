package com.example.journalapplication;

import android.app.Application;

public class JournalApi extends Application {//singleton class
    private String userid;
    private String userName;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static JournalApi instance;


    public static JournalApi getInstance(){
        if(instance == null){
            instance = new JournalApi();
        }
        return instance;
    }
    public JournalApi() {
    }

    public JournalApi(String userId, String userName) {
        this.userid = userId;
        this.userName = userName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
