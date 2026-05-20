package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.app_week_2.models.Review;
import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Query("SELECT * FROM reviews WHERE phoneId = :phoneId ORDER BY timestamp DESC")
    List<Review> getReviewsForPhone(String phoneId);

    @Query("SELECT AVG(rating) FROM reviews WHERE phoneId = :phoneId")
    float getAverageRating(String phoneId);
}
