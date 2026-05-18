package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.app_week_2.models.FavoritePhone;
import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoritePhone phone);

    @Delete
    void delete(FavoritePhone phone);

    @Query("SELECT * FROM favorites")
    List<FavoritePhone> getAll();

    @Query("SELECT * FROM favorites WHERE name = :name LIMIT 1")
    FavoritePhone findByName(String name);

    @Query("DELETE FROM favorites WHERE name = :name")
    void deleteByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FavoritePhone> favorites);
}