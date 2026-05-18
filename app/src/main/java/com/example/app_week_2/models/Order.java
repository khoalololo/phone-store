package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;           // e.g. "18 May 2026"
    public double total;
    public String itemsSummary;   // e.g. "iPhone 15 Pro x1, Galaxy S24 x2"
    public int itemCount;

    public Order() {}

    public Order(String date, double total, String itemsSummary, int itemCount) {
        this.date = date;
        this.total = total;
        this.itemsSummary = itemsSummary;
        this.itemCount = itemCount;
    }
}