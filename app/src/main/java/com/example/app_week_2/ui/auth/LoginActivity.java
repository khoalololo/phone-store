package com.example.app_week_2.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.SessionManager;
import com.example.app_week_2.data.UserDao;
import com.example.app_week_2.models.User;
import com.example.app_week_2.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                UserDao dao = AppDatabase.getInstance(this).userDao();
                User user = dao.login(username, password);

                runOnUiThread(() -> {
                    if (user != null) {
                        // Save session before moving to Home
                        SessionManager session = new SessionManager(this);
                        session.saveSession(user.getUsername(), user.getEmail());

                        Toast.makeText(this, "Welcome back " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish(); // Close login screen
                    } else {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        TextView signupBtn = findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(v->{
            startActivity(new Intent(this, RegisterActivity.class));
        });

        TextView browseBtn = findViewById(R.id.browseBtn);
        browseBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
