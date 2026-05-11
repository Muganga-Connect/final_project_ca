package com.example.mugangaconnect.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mugangaconnect.activity.LoginActivity;
import com.example.mugangaconnect.activity.NotificationPreferencesActivity;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MugangaFcmService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "muganga_reminders";

    @Override
    public void onNewToken(String token) {
        FirebaseUser user = new AuthRepository().getCurrentUser();
        if (user != null) {
            new AuthRepository().updateFcmToken(user.getUid(), token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        android.content.SharedPreferences prefs = getSharedPreferences(
                NotificationPreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(NotificationPreferencesActivity.KEY_PUSH_NOTIFICATIONS, true)) return;

        String type = message.getData().get("type");
        if ("appointment".equals(type) &&
                !prefs.getBoolean(NotificationPreferencesActivity.KEY_APPOINTMENT_REMINDERS, true)) return;

        String title, body;
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body  = message.getNotification().getBody();
        } else {
            title = message.getData().get("title");
            body  = message.getData().get("body");
        }
        if (title == null || body == null) return;

        createChannel();

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginActivity.class),
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Appointment Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);
        }
    }
}
