package com.example.app_week_2.data.repository;

import android.content.Context;

import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.FavoriteDao;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.FavoritePhone;

import java.util.List;

public class FavoriteRepository {

    private FavoriteDao dao;

    public FavoriteRepository(Context context) {
        dao = AppDatabase.getInstance(context).favoriteDao();
    }

    public void addFavorite(FavoritePhone phone) {
        new Thread(() -> {
            dao.insert(phone);
            FirestoreManager.syncFavorite(phone);
        }).start();
    }

    public void removeFavorite(String name) {
        new Thread(() -> {
            dao.deleteByName(name);
            FirestoreManager.removeFavorite(name);
        }).start();
    }

    public List<FavoritePhone> getAllLocal() {
        return dao.getAll();
    }

    public void syncFromCloud(Runnable onComplete) {
        FirestoreManager.downloadFavorites(cloudList -> {
            new Thread(() -> {
                for (FavoritePhone p : cloudList) {
                    if (dao.findByName(p.getName()) == null) {
                        dao.insert(p);
                    }
                }
                if (onComplete != null) onComplete.run();
            }).start();
        });
    }
}
