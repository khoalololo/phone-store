package com.example.app_week_2.ui.detail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.models.Phone;
import java.util.Locale;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.CartDao;
import com.example.app_week_2.data.FavoriteDao;
import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.models.FavoritePhone;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.app_week_2.ui.home.HomeActivity;
import com.example.app_week_2.ui.home.FavoritesActivity;
import com.example.app_week_2.ui.home.CartActivity;
import com.example.app_week_2.ui.auth.ProfileActivity;


public class PhoneDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_detail);

        Phone phone = (Phone) getIntent().getSerializableExtra("phone");

        if (phone == null) {
            Toast.makeText(this, "Error: Phone details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateUI(phone);

        FavoriteDao favoriteDao = AppDatabase.getInstance(this).favoriteDao();
        CartDao cartDao         = AppDatabase.getInstance(this).cartDao();

        Button favoriteBtn  = findViewById(R.id.favoriteBtn);
        Button addToCartBtn = findViewById(R.id.addToCartBtn);

// Check if already favorited and update button label
        new Thread(() -> {
            FavoritePhone existing = favoriteDao.findByName(phone.getName());
            runOnUiThread(() -> {
                if (existing != null) favoriteBtn.setText("❤️  Saved");
            });
        }).start();

        favoriteBtn.setOnClickListener(v -> {
            new Thread(() -> {
                FavoritePhone existing = favoriteDao.findByName(phone.getName());
                if (existing != null) {
                    favoriteDao.deleteByName(phone.getName());
                    runOnUiThread(() -> {
                        favoriteBtn.setText("❤️  Add to Favorites");
                        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    favoriteDao.insert(FavoritePhone.fromPhone(phone));
                    runOnUiThread(() -> {
                        favoriteBtn.setText("❤️  Saved");
                        Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        addToCartBtn.setOnClickListener(v -> {
            new Thread(() -> {
                CartItem existing = cartDao.findByName(phone.getName());
                if (existing != null) {
                    existing.quantity++;
                    cartDao.update(existing);
                } else {
                    cartDao.insert(CartItem.fromPhone(phone));
                }
                runOnUiThread(() -> {
                    showNotification(phone.getName());
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Added to cart!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("VIEW CART", v2 -> {
                        startActivity(new Intent(this, CartActivity.class));
                    });
                    snackbar.show();
                });
            }).start();
        });


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
    }

    private void showNotification(String phoneName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        String channelId = "cart_notifications";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Cart", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Added to Cart")
                .setContentText(phoneName + " has been added to your cart.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

    private void populateUI(Phone phone) {
        ((ImageView) findViewById(R.id.detailPhoneImage)).setImageResource(phone.getImageResource());
        ((TextView) findViewById(R.id.detailPhoneBrand)).setText(phone.getBrand());
        ((TextView) findViewById(R.id.detailPhoneName)).setText(phone.getName());
        ((TextView) findViewById(R.id.detailPhoneRating)).setText(String.valueOf(phone.getRating()));
        ((TextView) findViewById(R.id.detailPhonePrice)).setText(String.format(Locale.getDefault(), "$%.2f", phone.getPrice()));
        ((TextView) findViewById(R.id.detailPhoneDescription)).setText(phone.getDescription());
        ((TextView) findViewById(R.id.detailStorage)).setText(phone.getStorage());
        ((TextView) findViewById(R.id.detailBattery)).setText(phone.getBattery());
        ((TextView) findViewById(R.id.detailDisplay)).setText(phone.getDisplay());
        ((TextView) findViewById(R.id.detailOS)).setText(phone.getOs());
    }
}
