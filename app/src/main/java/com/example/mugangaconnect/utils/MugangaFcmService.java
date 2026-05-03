package com.example.mugangaconnect.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mugangaconnect.activity.AppointmentManagementActivity;
import com.example.mugangaconnect.activity.LoginActivity;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MugangaFcmService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "muganga_reminders";
    private static final AtomicInteger notificationId = new AtomicInteger(0);

    @Override
    public void onNewToken(String token) {
        AuthRepository authRepository = new AuthRepository();
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.saveFcmToken(token);

        FirebaseUser user = authRepository.getCurrentUser();
        if (user != null) {
            authRepository.updateFcmToken(user.getUid(), token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (message.getNotification() == null) return;
        SharedPreferences prefs = getSharedPreferences("MugangaConnectPrefs", MODE_PRIVATE);
        boolean appointmentEnabled = prefs.getBoolean("notification_appointmentReminders", true);
        boolean pushEnabled = prefs.getBoolean("notification_pushNotifications", true);
        if (!appointmentEnabled || !pushEnabled) return;

        String title = message.getNotification().getTitle();
        String body  = message.getNotification().getBody();
        if (title == null || title.trim().isEmpty()) title = "New notification";
        if (body == null) body = "";

        createChannel();

        Intent intent = buildNotificationIntent(message.getData());
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId.incrementAndGet(), builder.build());
    }

    private Intent buildNotificationIntent(Map<String, String> data) {
        String target = data != null ? data.get("target") : null;
        String appointmentId = data != null ? data.get("appointmentId") : null;
        Class<?> destination = "appointment".equalsIgnoreCase(target) || appointmentId != null
                ? AppointmentManagementActivity.class
                : LoginActivity.class;
        Intent intent = new Intent(this, destination);
        if (appointmentId != null) {
            intent.putExtra("appointmentId", appointmentId);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
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
