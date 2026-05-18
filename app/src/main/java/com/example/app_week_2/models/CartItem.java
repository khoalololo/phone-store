package com.example.app_week_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String brand;
    public String name;
    public int imageResource;
    public double price;
    public int quantity;

    public CartItem(String brand, String name, int imageResource, double price, int quantity) {
        this.brand = brand;
        this.name = name;
        this.imageResource = imageResource;
        this.price = price;
        this.quantity = quantity;
    }

    public double getSubtotal(){
        return price * quantity;
    }

    public static CartItem fromPhone(Phone phone) {
        return new CartItem(
                phone.getBrand(), phone.getName(),
                phone.getImageResource(), phone.getPrice(), 1
        );
    }
}
