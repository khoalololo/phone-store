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

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.register_username);
        passwordInput = findViewById(R.id.register_password);
        emailInput = findViewById(R.id.register_email);
        Button registerBtn = findViewById(R.id.registerBtn);
        TextView backToLogin = findViewById(R.id.backToLogin);
        TextView browseBtn = findViewById(R.id.browseBtn);

        registerBtn.setOnClickListener(v -> {
            String email    = emailInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                UserDao dao = AppDatabase.getInstance(this).userDao();
                
                // Check if user already exists by email or username
                User existingEmail = dao.findByEmail(email);
                User existingUser  = dao.findByUsername(username);

                runOnUiThread(() -> {
                    if (existingEmail != null) {
                        Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                    } else if (existingUser != null) {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        // All good, perform insertion
                        new Thread(() -> {
                            User newUser = new User(username, email, password);
                            dao.insert(newUser);

                            runOnUiThread(() -> {
                                // Save session for new user
                                SessionManager session = new SessionManager(this);
                                session.saveSession(newUser.getUsername(), newUser.getEmail());

                                Toast.makeText(this, "Account created successfully! Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomeActivity.class));
                                finish(); // Prevent going back to registration
                            });
                        }).start();
                    }
                });
            }).start();
        });

        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        browseBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
