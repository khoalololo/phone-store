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

import com.example.app_week_2.data.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepository = new UserRepository(this);
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

            userRepository.register(username, email, password, new UserRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show());
                }
            });
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
