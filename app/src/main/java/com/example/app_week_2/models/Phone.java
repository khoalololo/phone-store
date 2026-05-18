package com.example.app_week_2.models;

import java.io.Serializable;

public class Phone implements Serializable {
    private String brand;
    private String name;
    private int imageResource;
    private double price;
    private String storage;
    private String battery;
    private String display;
    private String os;
    private String description;
    private float rating;

    public Phone(String brand, String name, int imageResource, double price, String storage, String battery, String display, String os, String description, float rating) {
        this.brand = brand;
        this.name = name;
        this.imageResource = imageResource;
        this.price = price;
        this.storage = storage;
        this.battery = battery;
        this.display = display;
        this.os = os;
        this.description = description;
        this.rating = rating;
    }

    // Getters and Setters...
    public String getBrand() { return brand; }
    public String getName() { return name; }
    public int getImageResource() { return imageResource; }
    public double getPrice() { return price; }
    public String getStorage() { return storage; }
    public String getBattery() { return battery; }
    public String getDisplay() { return display; }
    public String getOs() { return os; }
    public String getDescription() { return description; }
    public float getRating() { return rating; }
}
