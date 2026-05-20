package com.example.app_week_2.data.repository;

import android.content.Context;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.PhoneDao;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.Phone;
import java.util.List;

public class PhoneRepository {

    private PhoneDao dao;

    public PhoneRepository(Context context) {
        dao = AppDatabase.getInstance(context).phoneDao();
    }

    // Called by HomeActivity to returns local data immediately,
    // then syncs from cloud in background
    public List<Phone> getAllLocal() {
        return dao.getAll();
    }

    public void syncFromCloud(Runnable onComplete) {
        FirestoreManager.downloadPhones(cloudPhones -> {
            new Thread(() -> {
                if (!cloudPhones.isEmpty()) {
                    dao.insertAll(cloudPhones);  // REPLACE handles updates
                }
                if (onComplete != null) onComplete.run();
            }).start();
        });
    }

    // Seed initial data to both local and Firestore
    public void seedDatabase(List<Phone> phones) {
        new Thread(() -> {
            dao.insertAll(phones);
            for (Phone phone : phones) {
                FirestoreManager.uploadPhone(phone);
            }
        }).start();
    }
}