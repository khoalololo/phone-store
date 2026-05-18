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
            // 1. Try local Room
            User localUser = dao.findByUsername(identifier);
            if (localUser == null) localUser = dao.findByEmail(identifier);

            if (localUser != null) {
                performFirebaseAuth(localUser, password, callback);
            } else {
                // 2. Try Firestore (User might be on a new device)
                FirestoreManager.downloadUserByUsername(identifier, cloudUser -> {
                    if (cloudUser != null) {
                        performFirebaseAuth(cloudUser, password, callback);
                    } else {
                        FirestoreManager.downloadUserByEmail(identifier, cloudUserByEmail -> {
                            if (cloudUserByEmail != null) {
                                performFirebaseAuth(cloudUserByEmail, password, callback);
                            } else {
                                callback.onFailure("User not found. Please register.");
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void performFirebaseAuth(User user, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(user.getEmail(), password)
                .addOnSuccessListener(result -> {
                    new Thread(() -> {
                        // Ensure user is in local DB for next time
                        if (dao.findByEmail(user.getEmail()) == null) {
                            dao.insert(user);
                        }
                        session.saveSession(user.getUsername(), user.getEmail());
                        callback.onSuccess();
                    }).start();
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
