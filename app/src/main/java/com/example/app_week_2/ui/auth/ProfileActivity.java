package com.example.app_week_2.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.SessionManager;
import com.example.app_week_2.ui.home.CartActivity;
import com.example.app_week_2.ui.home.HomeActivity;
import com.example.app_week_2.ui.home.OrderHistoryActivity;
import com.example.app_week_2.ui.home.FavoritesActivity;
import com.example.app_week_2.ui.admin.AdminDashboardActivity;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager session = new SessionManager(this);

        String username = session.getUsername();
        String email    = session.getEmail();

        ((TextView) findViewById(R.id.profileName)).setText(username);
        ((TextView) findViewById(R.id.profileEmail)).setText(email);
        ((TextView) findViewById(R.id.profileUsernameValue)).setText(username);
        ((TextView) findViewById(R.id.profileEmailValue)).setText(email);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        ((LinearLayout) findViewById(R.id.browseRow)).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        ((Button) findViewById(R.id.signOutBtn)).setOnClickListener(v -> {
            session.clearSession();  // wipe SharedPreferences
            Intent intent = new Intent(this, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        findViewById(R.id.orderHistoryRow).setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class))
        );
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.navFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.navCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        LinearLayout adminRow = findViewById(R.id.adminRow);
        if (session.isAdmin()) {
            adminRow.setVisibility(View.VISIBLE);
            adminRow.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminDashboardActivity.class))
            );
        }

    }
}