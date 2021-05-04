package com.example.duofinder.DB.POJO;

import java.util.Date;

public class Chat {
    public String username;
    public String userId;
    public String message;
    public long time;

    public Chat(){

    }

    public Chat(String username, String userId, String message) {
        this.username = username;
        this.userId = userId;
        this.message = message;
        time = new Date().getTime();
    }
}
