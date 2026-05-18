package com.example.app_week_2.data.remote;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthManager {

    public static void signInAnonymously() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            Log.d("AUTH", "Already signed in");
            return;
        }

        auth.signInAnonymously()
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    Log.d("AUTH", "Signed in: " + uid);
                })
                .addOnFailureListener(e -> {
                    Log.e("AUTH", "Auth failed", e);
                });
    }
}