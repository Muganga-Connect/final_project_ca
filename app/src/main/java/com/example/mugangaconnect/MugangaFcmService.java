package com.example.mugangaconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MugangaFcmService extends FirebaseMessagingService {

    private static final String TAG = "MugangaFcmService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences prefs = getSharedPreferences("MugangaConnectPrefs", Context.MODE_PRIVATE);
        boolean allowPush = prefs.getBoolean(NotificationPreferencesActivity.KEY_PUSH_NOTIFICATIONS, true);

        if (!allowPush) {
            Log.d(TAG, "Push notifications are disabled in preferences.");
            return;
        }

        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        // Notification display logic
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
