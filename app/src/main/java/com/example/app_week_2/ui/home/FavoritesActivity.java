package com.example.app_week_2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.FavoriteDao;
import com.example.app_week_2.models.FavoritePhone;
import com.example.app_week_2.models.Phone;
import com.example.app_week_2.data.PhoneProvider;
import com.example.app_week_2.ui.auth.ProfileActivity;
import com.example.app_week_2.ui.detail.PhoneDetailActivity;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ListView listView;
    private LinearLayout emptyState;
    private FavoriteDao dao;
    private List<FavoritePhone> favorites;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        dao       = AppDatabase.getInstance(this).favoriteDao();
        listView  = findViewById(R.id.favoritesList);
        emptyState = findViewById(R.id.emptyState);

        setupBottomNav();
        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // refresh when returning from detail
    }

    private void loadFavorites() {
        new Thread(() -> {
            favorites = dao.getAll();
            runOnUiThread(() -> {
                if (favorites.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    adapter = new FavoriteAdapter(this, R.layout.item_favorite, favorites);
                    listView.setAdapter(adapter);

                    // Jump to details
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        FavoritePhone fav = favorites.get(position);
                        Phone phone = null;
                        for (Phone p : PhoneProvider.getPhones()) {
                            if (p.getName().equalsIgnoreCase(fav.name)) {
                                phone = p;
                                break;
                            }
                        }
                        if (phone != null) {
                            Intent intent = new Intent(this, PhoneDetailActivity.class);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                        }
                    });

                    // Tap ❤️ to remove
                    adapter.setOnRemoveListener(phone -> {
                        new Thread(() -> {
                            dao.deleteByName(phone.name);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                loadFavorites();
                            });
                        }).start();
                    });
                }
            });
        }).start();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));
        // Favorites = current screen, no action
        findViewById(R.id.navCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}