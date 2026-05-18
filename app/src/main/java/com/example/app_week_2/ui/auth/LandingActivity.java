package com.example.app_week_2.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.ui.home.HomeActivity;
import com.example.app_week_2.data.SessionManager;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check session BEFORE setContentView — skip landing if already logged in
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_landing);

        Button signupBtn = findViewById(R.id.signupBtn);
        Button loginBtn  = findViewById(R.id.loginBtn);
        TextView browseBtn = findViewById(R.id.browseBtn);

        signupBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        loginBtn.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        browseBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class))
        );
    }
}
