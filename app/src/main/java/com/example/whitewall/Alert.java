package com.example.whitewall;

import android.graphics.drawable.Drawable;

public class Alert {
    String title,description,time;

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setTime(String time) { this.time = time; }

    public Alert(){}

    public Alert(String title, String description, String time) {
        this.title = title;
        this.description = description;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }


}
