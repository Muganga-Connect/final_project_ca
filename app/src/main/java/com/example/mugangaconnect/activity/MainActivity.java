package com.example.mugangaconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.NoShowPredictor;
import com.example.mugangaconnect.utils.SessionManager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private AppointmentRepository appointmentRepo;
    private FirebaseFirestore firestore;
    private ImageView dashboardProfileImage;
    private TextView tvWelcomeName;
    private TextView tvSmartAlertBody;
    private TextView tvUpcomingDoctor;
    private TextView tvUpcomingDateTime;
    private TextView tvUpcomingStatus;
    private Button btnReschedule;
    private Button btnCancelAppointment;
    private Button btnMarkAttended;
    private Button btnMarkMissed;
    private Button btnEnableReminder;
    private Appointment currentUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);
        firestore = FirebaseFirestore.getInstance();
        bindViews();

        bindUserName();
        loadDashboardData();
        wireButtons();

        BottomNavHelper.setup(this, BottomNavHelper.Screen.DASHBOARD);
    }

    private void bindViews() {
        dashboardProfileImage = findViewById(R.id.dashboardProfileImage);
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvSmartAlertBody = findViewById(R.id.tvSmartAlertBody);
        tvUpcomingDoctor = findViewById(R.id.tvUpcomingDoctor);
        tvUpcomingDateTime = findViewById(R.id.tvUpcomingDateTime);
        tvUpcomingStatus = findViewById(R.id.tvUpcomingStatus);
        btnReschedule = findViewById(R.id.btnReschedule);
        btnCancelAppointment = findViewById(R.id.btnCancelAppointment);
        btnMarkAttended = findViewById(R.id.btnMarkAttended);
        btnMarkMissed = findViewById(R.id.btnMarkMissed);
        btnEnableReminder = findViewById(R.id.btnEnableReminder);
    }

    private void bindUserName() {
        String name = session.getFullName();
        if (tvWelcomeName != null && name != null && !name.isEmpty()) {
            tvWelcomeName.setText(name);
        }
        
        // Load profile image
        loadProfileImage();
    }
    
    private void loadProfileImage() {
        String uid = session.getUid();
        if (uid == null) return;
        
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImageUrl = documentSnapshot.getString("profilePicture");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this).load(profileImageUrl).into(dashboardProfileImage);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Log error but don't show to user
                    android.util.Log.e("MainActivity", "Error loading profile image", e);
                });
    }

    private void loadDashboardData() {
        String uid = session.getUid();
        if (uid == null) return;

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onResult(List<Appointment> data) {
                // Find next upcoming appointment
                Appointment next = null;
                for (Appointment a : data) {
                    if (Appointment.Status.UPCOMING.equals(a.getStatus())) {
                        if (next == null || a.getDate().compareTo(next.getDate()) < 0) next = a;
                    }
                }
                currentUpcoming = next;

                // Compute risk from stats
                appointmentRepo.getMissedStats(uid, new AppointmentRepository.Callback<int[]>() {
                    @Override
                    public void onResult(int[] stats) {
                        String risk = NoShowPredictor.predict(stats[0], stats[1]);
                        runOnUiThread(() -> updateDashboardUI(currentUpcoming, risk, stats[0], stats[1]));
                    }
                    @Override public void onError(String message) {}
                });
            }
            @Override public void onError(String message) {}
        });
    }

    private void updateDashboardUI(Appointment upcoming, String risk, int missed, int total) {
        View content = findViewById(R.id.appointmentContent);
        View empty = findViewById(R.id.emptyAppointmentState);

        if (upcoming != null) {
            if (content != null) content.setVisibility(View.VISIBLE);
            if (empty != null) empty.setVisibility(View.GONE);

            if (tvUpcomingDoctor != null) tvUpcomingDoctor.setText(upcoming.getDoctorName());
            if (tvUpcomingDateTime != null) tvUpcomingDateTime.setText(upcoming.getDate() + "\n" + upcoming.getTime());
            if (tvUpcomingStatus != null) tvUpcomingStatus.setText(upcoming.getStatusValue());
        } else {
            if (content != null) content.setVisibility(View.GONE);
            if (empty != null) empty.setVisibility(View.VISIBLE);
        }
        
        // Update smart alert based on prediction
        if (tvSmartAlertBody != null) {
            if (total >= 3 && missed > 0) {
                tvSmartAlertBody.setText(missed + " of last " + total + " appointments were missed.");
            } else {
                tvSmartAlertBody.setText("You're on track! Keep attending your appointments.");
            }
        }
    }

    private void wireButtons() {
        if (findViewById(R.id.tvUpcomingStatus) != null) {
            findViewById(R.id.tvUpcomingStatus).setOnClickListener(v ->
                startActivity(new Intent(this, AppointmentHistoryActivity.class)));
        }

        if (btnReschedule != null) {
            btnReschedule.setOnClickListener(v ->
                    startActivity(new Intent(this, AppointmentManagementActivity.class)));
        }

        if (btnCancelAppointment != null) {
            btnCancelAppointment.setOnClickListener(v -> {
                if (currentUpcoming != null) {
                    updateAppointmentStatus(currentUpcoming.getId(), Appointment.Status.CANCELLED.name());
                }
            });
        }

        if (btnMarkAttended != null) {
            btnMarkAttended.setOnClickListener(v -> {
                if (currentUpcoming != null) {
                    updateAppointmentStatus(currentUpcoming.getId(), Appointment.Status.ATTENDED.name());
                }
            });
        }

        if (btnMarkMissed != null) {
            btnMarkMissed.setOnClickListener(v -> {
                if (currentUpcoming != null) {
                    updateAppointmentStatus(currentUpcoming.getId(), Appointment.Status.MISSED.name());
                }
            });
        }

        if (btnEnableReminder != null) {
            btnEnableReminder.setOnClickListener(v ->
                    Toast.makeText(this, "Early reminder enabled for upcoming appointments", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateAppointmentStatus(String id, String status) {
        String uid = session.getUid();
        appointmentRepo.updateStatus(id, uid, status, new AppointmentRepository.Callback<Void>() {
            @Override
            public void onResult(Void data) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Appointment marked as " + status, Toast.LENGTH_SHORT).show();
                    loadDashboardData();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
