package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.app_week_2.models.Phone;
import java.util.List;

@Dao
public interface PhoneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Phone> phones);

    @Query("SELECT * FROM phones")
    List<Phone> getAll();

    @Query("SELECT * FROM phones WHERE brand = :brand")
    List<Phone> getByBrand(String brand);

    @Query("SELECT COUNT(*) FROM phones")
    int count();
}