package com.example.app_week_2.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import android.content.Context;
import com.example.app_week_2.models.FavoritePhone;
import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.models.Order;
import com.example.app_week_2.models.Phone;
import com.example.app_week_2.data.FavoriteDao;
import com.example.app_week_2.data.CartDao;
import com.example.app_week_2.data.OrderDao;
import com.example.app_week_2.models.User;

import com.example.app_week_2.models.Review;

@Database(entities = {User.class, FavoritePhone.class, CartItem.class, Order.class, Phone.class, Review.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract FavoriteDao favoriteDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();
    public abstract PhoneDao phoneDao();
    public abstract ReviewDao reviewDao();

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
