package com.example.app_week_2.data.repository;

import android.content.Context;

import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.CartDao;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.CartItem;

import java.util.List;

public class CartRepository {

    private CartDao dao;

    public CartRepository(Context context) {
        dao = AppDatabase.getInstance(context).cartDao();
    }

    public void addToCart(CartItem item) {
        new Thread(() -> {
            CartItem existing = dao.findByName(item.name);
            if (existing != null) {
                existing.quantity++;
                dao.update(existing);
            } else {
                dao.insert(item);
            }
            syncToCloud();
        }).start();
    }

    public void updateQuantity(CartItem item, int qty) {
        new Thread(() -> {
            if (qty <= 0) {
                dao.deleteById(item.id);
            } else {
                item.quantity = qty;
                dao.update(item);
            }
            syncToCloud();
        }).start();
    }

    public void removeFromCart(CartItem item) {
        new Thread(() -> {
            dao.deleteById(item.id);
            syncToCloud();
        }).start();
    }

    private void syncToCloud() {
        List<CartItem> all = dao.getAll();
        FirestoreManager.syncCart(all);
    }
}
