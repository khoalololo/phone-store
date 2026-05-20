package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String brand;
    public String name;
    public String imageName; // Changed from int imageResource to String imageName
    public double price;
    public int quantity;

    public CartItem() {}

    public CartItem(String brand, String name, String imageName, double price, int quantity) {
        this.brand = brand;
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.quantity = quantity;
    }

    public double getSubtotal(){
        return price * quantity;
    }

    public static CartItem fromPhone(Phone phone) {
        return new CartItem(
                phone.getBrand(), phone.getName(),
                phone.getImageName(), phone.getPrice(), 1
        );
    }
}
