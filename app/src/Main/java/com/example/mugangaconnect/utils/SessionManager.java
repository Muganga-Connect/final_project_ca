package com.example.mugangaconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {

    private static final String PREF_NAME = "muganga_session";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Unable to initialize encrypted session storage", e);
        }
    }

    public void saveSession(String uid, String fullName, String email) {
        requireNonEmpty(uid, "uid");
        requireNonEmpty(email, "email");
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_NAME, safe(fullName))
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, prefs.getString(KEY_PHONE, ""))
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public void saveSession(String uid, String fullName, String email, String phone) {
        requireNonEmpty(uid, "uid");
        requireNonEmpty(email, "email");
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_NAME, safe(fullName))
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, safe(phone))
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public void saveFcmToken(String token) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public boolean isLoggedIn() { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    public String getUid() { return prefs.getString(KEY_UID, null); }
    public String getFullName() { return prefs.getString(KEY_NAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getPhone() { return prefs.getString(KEY_PHONE, ""); }
    public String getFcmToken() { return prefs.getString(KEY_FCM_TOKEN, ""); }

    private void requireNonEmpty(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
