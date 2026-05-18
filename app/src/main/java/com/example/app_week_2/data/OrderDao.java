package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.app_week_2.models.Order;
import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    void insert(Order order);

    // Most recent first
    @Query("SELECT * FROM orders ORDER BY id DESC")
    List<Order> getAll();
}