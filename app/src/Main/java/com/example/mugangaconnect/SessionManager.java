package com.example.mugangaconnect;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MugangaConnectPrefs";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void updateFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
    }

    public String getFcmToken() {
        return pref.getString(KEY_FCM_TOKEN, null);
    }
}
