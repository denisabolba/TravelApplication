package com.example.notesapplication;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Note {
    String title;
    String content;
    Timestamp timestamp;
    String date;
    String endDate;
    String time;
    String currentDate;
    String location;



    public Note(String title, String content, String date, String endDate, String time, String currentDate, String location) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.endDate = endDate;
        this.time = time;
        this.currentDate = currentDate;
        this.location = location;
    }

    public Note() {
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}