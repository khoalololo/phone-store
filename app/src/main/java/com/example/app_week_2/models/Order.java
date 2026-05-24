package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;
    public double total;
    public String itemsSummary;
    public int itemCount;

    public Order() {}

    public Order(String date, double total, String itemsSummary, int itemCount) {
        this.date = date;
        this.total = total;
        this.itemsSummary = itemsSummary;
        this.itemCount = itemCount;
    }
}