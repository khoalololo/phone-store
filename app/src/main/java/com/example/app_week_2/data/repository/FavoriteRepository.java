package com.example.app_week_2.data.repository;

import android.content.Context;

import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.FavoriteDao;
import com.example.app_week_2.data.PhoneProvider;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.FavoritePhone;
import com.example.app_week_2.models.Phone;
import android.util.Log;

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
                    if (p != null && p.getName() != null) {
                        // Data Repair: If imageName is missing, recover it from PhoneProvider
                        if (p.imageName == null || p.imageName.isEmpty()) {
                            for (Phone ref : PhoneProvider.getPhones()) {
                                if (ref.getName().equalsIgnoreCase(p.getName())) {
                                    p.imageName = ref.getImageName();
                                    Log.d("FAV_REPO", "Repaired missing image for: " + p.getName() + " -> " + p.imageName);
                                    break;
                                }
                            }
                        }

                        FavoritePhone existing = dao.findByName(p.getName());
                        if (existing == null) {
                            dao.insert(p);
                        } else {
                            p.id = existing.id;
                            dao.insert(p);
                        }
                    }
                }
                if (onComplete != null) onComplete.run();
            }).start();
        });
    }
}
