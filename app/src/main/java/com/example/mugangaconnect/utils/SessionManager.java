package com.example.mugangaconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_LANGUAGE = "app_language";

    private SharedPreferences prefs;

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
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("SessionManager", "Error initializing EncryptedSharedPreferences", e);
            // Fallback to regular SharedPreferences if encryption fails
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveSession(String uid, String fullName, String email) {
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, prefs.getString(KEY_PHONE, ""))
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

    public void saveFcmToken(String token) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public boolean isLoggedIn() { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    
    public void saveLanguage(String languageCode) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public String getUid() { return prefs.getString(KEY_UID, null); }
    
    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }
    
    public boolean isBiometricEnabled() {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    public String getFullName() { return prefs.getString(KEY_NAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getPhone() { return prefs.getString(KEY_PHONE, ""); }
}