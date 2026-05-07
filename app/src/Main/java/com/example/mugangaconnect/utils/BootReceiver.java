package com.example.mugangaconnect.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                AppointmentRepository repository = new AppointmentRepository(context);
                repository.getForPatient(user.getUid(), new AppointmentRepository.Callback<List<Appointment>>() {
                    @Override
                    public void onResult(List<Appointment> data) {
                        for (Appointment appointment : data) {
                            if (Appointment.Status.UPCOMING.name().equals(appointment.getStatus())) {
                                ReminderManager.scheduleReminders(context, appointment);
                            }
                        }
                    }

                    @Override
                    public void onError(String message) {
                        // Log error
                    }
                });
            }
        }
    }
}
