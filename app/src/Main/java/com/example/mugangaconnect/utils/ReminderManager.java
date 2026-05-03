package com.example.mugangaconnect.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mugangaconnect.data.model.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderManager {

    private static final String TAG = "ReminderManager";
    private static final String PREFS_NAME = "MugangaConnectPrefs";

    public static void scheduleReminders(Context context, Appointment appointment) {
        long appointmentTime = getTimestamp(appointment.getDate(), appointment.getTime());
        if (appointmentTime <= System.currentTimeMillis()) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // 1 Day Before
        if (prefs.getBoolean("reminder_oneDayBefore", true)) {
            long triggerTime = appointmentTime - (24 * 60 * 60 * 1000);
            scheduleAlarm(context, appointment, triggerTime, "1 Day Reminder", 1);
        }

        // 2 Hours Before
        if (prefs.getBoolean("reminder_twoHoursBefore", true)) {
            long triggerTime = appointmentTime - (2 * 60 * 60 * 1000);
            scheduleAlarm(context, appointment, triggerTime, "2 Hours Reminder", 2);
        }

        // 30 Minutes Before
        if (prefs.getBoolean("reminder_thirtyMinBefore", false)) {
            long triggerTime = appointmentTime - (30 * 60 * 1000);
            scheduleAlarm(context, appointment, triggerTime, "30 Minutes Reminder", 3);
        }
    }

    private static void scheduleAlarm(Context context, Appointment appointment, long triggerTime, String type, int requestCodeOffset) {
        if (triggerTime <= System.currentTimeMillis()) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", "Upcoming Appointment");
        intent.putExtra("message", "Reminder: Your appointment with Dr. " + appointment.getDoctorName() + " is soon.");
        intent.putExtra("notificationId", appointment.getId().hashCode() + requestCodeOffset);

        int requestCode = appointment.getId().hashCode() + requestCodeOffset;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
            Log.d(TAG, "Scheduled " + type + " for " + appointment.getId() + " at " + new Date(triggerTime));
        }
    }

    static long getTimestamp(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date d = sdf.parse(date + " " + time);
            return d != null ? d.getTime() : 0;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date/time", e);
            return 0;
        }
    }

    public static void cancelReminders(Context context, String appointmentId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        
        for (int i = 1; i <= 3; i++) {
            int requestCode = appointmentId.hashCode() + i;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }
}
