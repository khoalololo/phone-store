package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoritePhone {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String brand;
    public String name;
    public int imageResource;
    public double price;
    public String storage;
    public String battery;
    public String display;
    public String os;

    public FavoritePhone(String brand, String name, int imageResource, double price,
                         String storage, String battery, String display, String os) {
        this.brand = brand;
        this.name = name;
        this.imageResource = imageResource;
        this.price = price;
        this.storage = storage;
        this.battery = battery;
        this.display = display;
        this.os = os;
    }

    public static FavoritePhone fromPhone(Phone phone) {
        return new FavoritePhone(
                phone.getBrand(), phone.getName(), phone.getImageResource(),
                phone.getPrice(), phone.getStorage(), phone.getBattery(),
                phone.getDisplay(), phone.getOs()
        );
    }

}
