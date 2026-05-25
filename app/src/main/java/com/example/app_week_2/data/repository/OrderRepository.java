package com.example.app_week_2.data.repository;

import android.content.Context;

import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.OrderDao;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.Order;

import java.util.List;

public class OrderRepository {

    private OrderDao dao;

    public OrderRepository(Context context) {
        dao = AppDatabase.getInstance(context).orderDao();
    }

    public void placeOrder(Order order, Runnable onComplete) {
        new Thread(() -> {
            long id = dao.insert(order);
            order.id = (int) id;
            FirestoreManager.syncOrder(order);
            if (onComplete != null) onComplete.run();
        }).start();
    }

    public List<Order> getAllLocal() {
        return dao.getAll();
    }

    public void cancelOrder(int orderId, Runnable onComplete) {
        new Thread(() -> {
            dao.deleteById(orderId);
            FirestoreManager.removeOrder(orderId);
            if (onComplete != null) onComplete.run();
        }).start();
    }

    public void syncFromCloud(Runnable onComplete) {
        FirestoreManager.downloadOrders(cloudList -> {
            new Thread(() -> {
                for (Order o : cloudList) {
                    // Check if already exists (simplified by date/total check or ID if managed well)
                    // For now, let's just insert if local is empty or based on summary
                    // Real app would use a UUID from Firestore
                    dao.insert(o);
                }
                if (onComplete != null) onComplete.run();
            }).start();
        });
    }
}
