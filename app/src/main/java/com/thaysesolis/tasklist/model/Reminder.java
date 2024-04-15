package com.thaysesolis.tasklist.model;

import java.time.Instant;
import java.time.ZoneId;

public class Reminder {
    private String title;
    private String text;
    private long id;
    private long creationTimestamp;

    public Reminder() {
        this.creationTimestamp = System.currentTimeMillis();
    }

    public Reminder(String title, String text, long id) {
        this();
        this.title = title;
        this.text = text;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", id=" + id +
                ", creationTimestamp=" + Instant.ofEpochMilli(creationTimestamp).atZone(ZoneId.systemDefault()) +
                '}';
    }
}
