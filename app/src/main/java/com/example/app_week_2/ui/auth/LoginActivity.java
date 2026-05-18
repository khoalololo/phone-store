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

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            String identifier = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            userRepository.login(identifier, password, new UserRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Failed: " + error, Toast.LENGTH_LONG).show());
                }
            });
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
