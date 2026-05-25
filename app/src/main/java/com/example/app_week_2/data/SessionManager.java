package com.example.app_week_2.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "user_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL    = "email";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_IS_ADMIN = "is_admin";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String username, String email, boolean isAdmin) {
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.apply();
    }

    // Keep the old saveSession(username, email) for compatibility — it sets isAdmin=false:
    public void saveSession(String username, String email) {
        saveSession(username, email, false);
    }


    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isAdmin() {
        return prefs.getBoolean(KEY_IS_ADMIN, false);
    }

}