package com.example.app_week_2.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.OrderDao;
import com.example.app_week_2.models.Order;
import android.content.Intent;
import com.example.app_week_2.ui.auth.ProfileActivity;

import java.util.List;

import com.example.app_week_2.data.repository.OrderRepository;

public class OrderHistoryActivity extends AppCompatActivity {

    private OrderRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        repository = new OrderRepository(this);
        ListView listView   = findViewById(R.id.orderList);
        LinearLayout empty  = findViewById(R.id.orderEmptyState);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Navigation
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.navFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.navCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        loadOrders();
        repository.syncFromCloud(this::loadOrders);
    }

    private void loadOrders() {
        ListView listView   = findViewById(R.id.orderList);
        LinearLayout empty  = findViewById(R.id.orderEmptyState);

        new Thread(() -> {
            List<Order> orders = repository.getAllLocal();
            runOnUiThread(() -> {
                if (orders.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                    String[] rows = new String[orders.size()];
                    for (int i = 0; i < orders.size(); i++) {
                        Order o = orders.get(i);
                        rows[i] = o.date + "   $" + String.format("%.2f", o.total)
                                + "\n" + o.itemsSummary;
                    }
                    listView.setAdapter(new ArrayAdapter<>(
                            this, android.R.layout.simple_list_item_1, rows
                    ));
                }
            });
        }).start();
    }
}
