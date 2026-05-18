package com.example.app_week_2.data.repository;

import android.content.Context;

import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.SessionManager;
import com.example.app_week_2.data.UserDao;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.User;
import com.google.firebase.auth.FirebaseAuth;

public class UserRepository {

    private UserDao dao;
    private FirebaseAuth auth;
    private SessionManager session;

    public UserRepository(Context context) {
        dao = AppDatabase.getInstance(context).userDao();
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(context);
    }

    public void register(String username, String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    User user = new User(username, email, password);
                    new Thread(() -> {
                        dao.insert(user);
                        session.saveSession(username, email);
                        FirestoreManager.syncUser(user);
                        callback.onSuccess();
                    }).start();
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void login(String identifier, String password, AuthCallback callback) {
        new Thread(() -> {
            User user = dao.findByUsername(identifier);
            if (user == null) user = dao.findByEmail(identifier);

            if (user == null) {
                callback.onFailure("User not found locally. Please register first.");
                return;
            }

            String email = user.getEmail();
            User finalUser = user;
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        new Thread(() -> {
                            session.saveSession(finalUser.getUsername(), finalUser.getEmail());
                            callback.onSuccess();
                        }).start();
                    })
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }).start();
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
