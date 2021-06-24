package com.example.rentalapp;

public class Alerts {
    String date,title,description,id;

    public Alerts(String date, String title, String description, String id) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public Alerts() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
