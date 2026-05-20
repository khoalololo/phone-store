package com.example.app_week_2.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "reviews")
public class Review implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String phoneId;
    public String username;
    public float rating;
    public String comment;
    public long timestamp;

    public Review() {}

    public Review(String phoneId, String username, float rating, String comment) {
        this.phoneId = phoneId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = System.currentTimeMillis();
    }
}
