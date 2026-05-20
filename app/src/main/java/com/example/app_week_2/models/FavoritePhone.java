package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoritePhone {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String brand;
    public String name;
    public String imageName; // Changed from int imageResource to String imageName
    public double price;
    public String storage;
    public String battery;
    public String display;
    public String os;

    public FavoritePhone() {}

    public FavoritePhone(String brand, String name, String imageName, double price,
                         String storage, String battery, String display, String os) {
        this.brand = brand;
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.storage = storage;
        this.battery = battery;
        this.display = display;
        this.os = os;
    }

    public static FavoritePhone fromPhone(Phone phone) {
        return new FavoritePhone(
                phone.getBrand(), phone.getName(), phone.getImageName(),
                phone.getPrice(), phone.getStorage(), phone.getBattery(),
                phone.getDisplay(), phone.getOs()
        );
    }

    public String getName() {
        return name;
    }

    // Compatibility with old Firestore data
    // Some older records might use 'imageResource' as an Integer ID or String name
    public void setImageResource(Object imageResource) {
        if (imageResource instanceof String) {
            String val = (String) imageResource;
            // If it's a numeric string, it's an old resource ID, we should ignore it
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
