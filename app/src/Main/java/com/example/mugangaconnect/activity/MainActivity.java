package com.example.mugangaconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private AppointmentRepository appointmentRepo;
    private Appointment upcomingAppointment;

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
        appointmentRepo = new AppointmentRepository(this);

        setupUI();
        loadData();
        
        BottomNavHelper.setup(this, BottomNavHelper.Screen.DASHBOARD);
    }

    private void setupUI() {
        // Set User Name
        TextView tvUserName = findViewById(R.id.tvUserName);
        if (tvUserName != null && session.getFullName() != null) {
            tvUserName.setText(session.getFullName().replace(" ", "\n"));
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

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onResult(List<Appointment> data) {
                for (Appointment appt : data) {
                    if (Appointment.Status.CONFIRMED.name().equals(appt.getStatus())) {
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
            public void onResult(Void data) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                    loadData(); // Refresh
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
