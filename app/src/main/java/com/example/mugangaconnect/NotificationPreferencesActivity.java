package com.example.mugangaconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationPreferencesActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MugangaConnectPrefs";
    public static final String KEY_APPOINTMENT_REMINDERS = "notification_appointmentReminders";
    public static final String KEY_PUSH_NOTIFICATIONS = "notification_pushNotifications";

    private Switch switchAppointment, switchPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preferences);

        switchAppointment = findViewById(R.id.switchAppointment);
        switchPush = findViewById(R.id.switchPush);
        loadPreferences();

        switchAppointment.setOnCheckedChangeListener((b, isChecked) -> savePreference(KEY_APPOINTMENT_REMINDERS, isChecked));
        switchPush.setOnCheckedChangeListener((b, isChecked) -> savePreference(KEY_PUSH_NOTIFICATIONS, isChecked));
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        switchAppointment.setChecked(prefs.getBoolean(KEY_APPOINTMENT_REMINDERS, true));
        switchPush.setChecked(prefs.getBoolean(KEY_PUSH_NOTIFICATIONS, true));
    }

    private void savePreference(String key, boolean value) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }
}
