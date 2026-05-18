package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.app_week_2.models.CartItem;
import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void insert(CartItem item);

    @Update
    void update(CartItem item);

    @Query("SELECT * FROM cart_items")
    List<CartItem> getAll();

    @Query("SELECT * FROM cart_items WHERE name = :name LIMIT 1")
    CartItem findByName(String name);

    @Query("DELETE FROM cart_items WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM cart_items")
    void clearAll();
}