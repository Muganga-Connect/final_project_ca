package com.example.mugangaconnect;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationPreferencesActivity extends AppCompatActivity {

    private Switch switchAppointment, switchPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preferences);

        switchAppointment = findViewById(R.id.switchAppointment);
        switchPush = findViewById(R.id.switchPush);
    }
}
