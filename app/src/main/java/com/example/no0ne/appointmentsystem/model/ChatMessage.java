package com.example.no0ne.appointmentsystem.model;

import java.util.Date;

/**
 * Created by no0ne on 3/30/18.
 */

public class ChatMessage {

    private String message;
    private String user_name;
    private long time;

    public ChatMessage() {
    }

    public ChatMessage(String message, String user_name) {
        this.message = message;
        this.user_name = user_name;

        time = new Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
