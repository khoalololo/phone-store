package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import com.example.app_week_2.models.Order;
import java.util.List;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    // Most recent first
    @Query("SELECT * FROM orders ORDER BY id DESC")
    List<Order> getAll();
}