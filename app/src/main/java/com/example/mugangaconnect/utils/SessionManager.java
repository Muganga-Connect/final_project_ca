package com.example.mugangaconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "muganga_session";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String uid, String fullName, String email) {
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public void saveSession(String uid, String fullName, String email, String phone) {
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, phone)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    public String getUid() { return prefs.getString(KEY_UID, null); }
    public String getFullName() { return prefs.getString(KEY_NAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getPhone() { return prefs.getString(KEY_PHONE, ""); }
}