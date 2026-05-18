package com.example.app_week_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.app_week_2.models.User;

@Dao
public interface UserDao
{
    @Insert
    void insert(User user);
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);
}
