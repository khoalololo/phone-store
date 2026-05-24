package com.example.app_week_2.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "phones")
public class Phone implements Serializable {
    @PrimaryKey
    @NonNull
    public String id = ""; 
    private String brand;
    private String name;
    public String imageName; 
    private double price;
    private String storage;
    private String battery;
    private String display;
    private String os;
    private String description;
    private float rating;
    private String chipset;
    private String camera;
    private String charging;
    private String features;

    public Phone() {}

    @Ignore
    public Phone(String brand, String name, String imageName, double price, String storage, 
                 String battery, String display, String os, String description, float rating,
                 String chipset, String camera, String charging, String features) {
        this.brand = brand;
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.storage = storage;
        this.battery = battery;
        this.display = display;
        this.os = os;
        this.description = description;
        this.rating = rating;
        this.chipset = chipset;
        this.camera = camera;
        this.charging = charging;
        this.features = features;
    }

    public Phone(String id, String brand, String name, String imageName, double price, String storage, 
                 String battery, String display, String os, String description, float rating,
                 String chipset, String camera, String charging, String features) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.storage = storage;
        this.battery = battery;
        this.display = display;
        this.os = os;
        this.description = description;
        this.rating = rating;
        this.chipset = chipset;
        this.camera = camera;
        this.charging = charging;
        this.features = features;
    }

    // Getters and Setters
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    public String getBattery() { return battery; }
    public void setBattery(String battery) { this.battery = battery; }
    public String getDisplay() { return display; }
    public void setDisplay(String display) { this.display = display; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getChipset() { return chipset; }
    public void setChipset(String chipset) { this.chipset = chipset; }
    public String getCamera() { return camera; }
    public void setCamera(String camera) { this.camera = camera; }
    public String getCharging() { return charging; }
    public void setCharging(String charging) { this.charging = charging; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
}
