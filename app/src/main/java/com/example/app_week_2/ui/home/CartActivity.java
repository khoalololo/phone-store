package com.example.app_week_2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.CartDao;
import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.ui.auth.ProfileActivity;

import java.util.List;

import com.example.app_week_2.data.repository.CartRepository;

public class CartActivity extends AppCompatActivity {

    private ListView listView;
    private LinearLayout emptyState;
    private TextView cartTotal;
    private CartDao dao;
    private List<CartItem> items;
    private CartAdapter adapter;
    private CartRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        repository = new CartRepository(this);
        dao        = AppDatabase.getInstance(this).cartDao();
        listView   = findViewById(R.id.cartList);
        emptyState = findViewById(R.id.cartEmptyState);
        cartTotal  = findViewById(R.id.cartTotal);

        findViewById(R.id.checkoutBtn).setOnClickListener(v -> {
            if (items == null || items.isEmpty()) return;
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
        });

        setupBottomNav();
        loadCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    void loadCart() {
        new Thread(() -> {
            items = dao.getAll();
            runOnUiThread(() -> {
                if (items.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                    cartTotal.setText("$0.00");
                } else {
                    emptyState.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                    adapter = new CartAdapter(this, R.layout.item_cart, items);
                    listView.setAdapter(adapter);

                    updateTotal();

                    // Quantity changes
                    adapter.setOnQuantityChangedListener((item, newQty) -> {
                        repository.updateQuantity(item, newQty);
                        // Refresh after a slight delay or by observing
                        // For simplicity, reload after background work
                        new Thread(() -> {
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            runOnUiThread(this::loadCart);
                        }).start();
                    });

                    // Remove
                    adapter.setOnRemoveListener(item -> {
                        repository.removeFromCart(item);
                        new Thread(() -> {
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            runOnUiThread(this::loadCart);
                        }).start();
                    });
                }
            });
        }).start();
    }

    private void updateTotal() {
        if (items == null) return;
        double total = 0;
        for (CartItem item : items) total += item.getSubtotal();
        cartTotal.setText(String.format("$%.2f", total));
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.navFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        // Cart = current screen, no action
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}