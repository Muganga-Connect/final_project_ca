package com.example.mugangaconnect.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.mugangaconnect.data.model.User;
import com.example.mugangaconnect.data.repository.AuthRepository;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AuthRepository authRepo;
    private SessionManager session;
    private AppointmentRepository appointmentRepo;
    private Appointment upcomingAppointment;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notifications disabled. You won't receive reminders.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        appointmentRepo = new AppointmentRepository();

        checkNotificationPermission();
        setupUI();
        loadData();
        
        BottomNavHelper.setup(this, BottomNavHelper.Screen.DASHBOARD);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupUI() {
        // Set User Name
        TextView tvUserName = findViewById(R.id.tvUserName);
        if (tvUserName != null) {
            tvUserName.setText("User"); // Simplified for now
        }

        // View History Button
        View btnHistory = findViewById(R.id.btnViewHistory);
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, AppointmentHistoryActivity.class));
            });
        }

        // Reschedule/Cancel buttons for upcoming appointment
        Button btnReschedule = findViewById(R.id.btnRescheduleMain);
        Button btnCancel = findViewById(R.id.btnCancelMain);

        if (btnReschedule != null) {
            btnReschedule.setOnClickListener(v -> {
                if (upcomingAppointment != null) {
                    // Navigate to management or handle here
                    startActivity(new Intent(MainActivity.this, AppointmentManagementActivity.class));
                } else {
                    Toast.makeText(this, "No upcoming appointment to reschedule", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                if (upcomingAppointment != null) {
                    cancelAppointment(upcomingAppointment);
                } else {
                    Toast.makeText(this, "No upcoming appointment to cancel", Toast.LENGTH_SHORT).show();
                }
            });
        }

        findViewById(R.id.btnEnableReminder).setOnClickListener(v -> 
                Toast.makeText(this, "Early reminders enabled", Toast.LENGTH_SHORT).show());
    }

    private void loadData() {

        String uid = session.getUid();
        if (uid == null) return;

        loadProfileImage(uid);

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> data) {
                for (Appointment appt : data) {
                    if (Appointment.Status.CONFIRMED.name().equals(appt.getStatus()) || 
                        Appointment.Status.UPCOMING.name().equals(appt.getStatus())) {
                        upcomingAppointment = appt;
                        updateUpcomingUI(appt);
                        break;
                    }
                }
            }

            @Override
            public void onError(String message) {
                // Handle error
            }
        });

    }

    private void updateUpcomingUI(Appointment appt) {
        runOnUiThread(() -> {
            TextView tvDoctor = findViewById(R.id.tvDoctorName);
            TextView tvTime = findViewById(R.id.tvAppointmentTime);
            TextView tvStatus = findViewById(R.id.tvAppointmentStatus);
            TextView tvDept = findViewById(R.id.tvDoctorDept);

            if (tvDoctor != null) tvDoctor.setText(appt.getDoctorName());
            if (tvTime != null) tvTime.setText(appt.getDate() + ", " + appt.getTime());
            if (tvStatus != null) tvStatus.setText(appt.getStatus());
            if (tvDept != null) tvDept.setText(appt.getDepartment());
        });
    }

    private void cancelAppointment(Appointment appt) {
        appointmentRepo.updateStatus(appt.getId(), session.getUid(), Appointment.Status.CANCELLED.name(), new AppointmentRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                    loadData();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadProfileImage(String uid) {
        authRepo.getProfile(uid, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    ImageView imgProfile = findViewById(R.id.ivDashboardProfile);
                    if (imgProfile != null && user.getProfileImageUrl() != null
                            && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(MainActivity.this)
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.user)
                                .circleCrop()
                                .into(imgProfile);
                    }
                });
            }
            @Override
            public void onError(String message) { }
        });
    }
}
