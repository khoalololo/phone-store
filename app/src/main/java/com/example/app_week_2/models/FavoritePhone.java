package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoritePhone {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String brand;
    public String name;
    public String imageName; 
    public double price;
    public String storage;
    public String battery;
    public String display;
    public String os;
    
    // New fields
    public String chipset;
    public String camera;
    public String charging;
    public String features;

    public FavoritePhone() {}

    public FavoritePhone(String brand, String name, String imageName, double price,
                         String storage, String battery, String display, String os,
                         String chipset, String camera, String charging, String features) {
        this.brand = brand;
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.storage = storage;
        this.battery = battery;
        this.display = display;
        this.os = os;
        this.chipset = chipset;
        this.camera = camera;
        this.charging = charging;
        this.features = features;
    }

    public static FavoritePhone fromPhone(Phone phone) {
        return new FavoritePhone(
                phone.getBrand(), phone.getName(), phone.getImageName(),
                phone.getPrice(), phone.getStorage(), phone.getBattery(),
                phone.getDisplay(), phone.getOs(),
                phone.getChipset(), phone.getCamera(), phone.getCharging(), phone.getFeatures()
        );
    }

    public String getName() {
        return name;
    }

    // Compatibility with old Firestore data
    public void setImageResource(Object imageResource) {
        if (imageResource instanceof String) {
            String val = (String) imageResource;
            if (!val.matches("\\d+")) {
                this.imageName = val;
            }
        }
    }

    public void setimageResource(Object imageResource) {
        setImageResource(imageResource);
    }

    public void setimageName(String imageName) {
        if (imageName != null && !imageName.matches("\\d+")) {
            this.imageName = imageName;
        }
    }
}
