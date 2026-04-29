package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity {

    private AppointmentRepository appointmentRepo;
    private SessionManager session;
    private final List<Appointment> appointments = new ArrayList<>();

    // UI Components - Updated for new layout
    private TextView nextCheckupTime;
    private EditText searchEditText;
    private MaterialButton confirmButton;
    private RecyclerView doctorRecyclerView;
    private RecyclerView timeSlotRecyclerView;
    private RecyclerView appointmentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_management);

        session = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);

        initializeViews();
        setupClickListeners();
        loadUserData();
        loadAppointments();

        // Note: BottomNavHelper.setup() needs to be implemented or commented out
        // BottomNavHelper.setup(this, BottomNavHelper.Screen.SCHEDULE);
    }

    private void initializeViews() {
        nextCheckupTime = findViewById(R.id.nextCheckupTime);
        searchEditText = findViewById(R.id.searchEditText);
        confirmButton = findViewById(R.id.confirmButton);
        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        timeSlotRecyclerView = findViewById(R.id.timeSlotRecyclerView);
        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView);
    }

    private void setupClickListeners() {
        // Search functionality
        searchEditText.setOnClickListener(v -> {
            Toast.makeText(this, "Opening search...", Toast.LENGTH_SHORT).show();
            // TODO: Implement search functionality
        });

        // Department selection buttons
        findViewById(R.id.btnCardiology).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by Cardiology...", Toast.LENGTH_SHORT).show();
            // TODO: Filter doctors by cardiology
        });

        findViewById(R.id.btnNeurology).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by Neurology...", Toast.LENGTH_SHORT).show();
            // TODO: Filter doctors by neurology
        });

        findViewById(R.id.btnDentistry).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by Dentistry...", Toast.LENGTH_SHORT).show();
            // TODO: Filter doctors by dentistry
        });

        findViewById(R.id.btnPediatrics).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by Pediatrics...", Toast.LENGTH_SHORT).show();
            // TODO: Filter doctors by pediatrics
        });

        // Hospital chips
        findViewById(R.id.chipKingFaisal).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by King Faisal Hospital...", Toast.LENGTH_SHORT).show();
            // TODO: Filter by hospital
        });

        findViewById(R.id.chipRwandaMilitary).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by Rwanda Military Hospital...", Toast.LENGTH_SHORT).show();
            // TODO: Filter by hospital
        });

        findViewById(R.id.chipCHUK).setOnClickListener(v -> {
            Toast.makeText(this, "Filtering by CHUK Hospital...", Toast.LENGTH_SHORT).show();
            // TODO: Filter by hospital
        });

        // Tab switching
        findViewById(R.id.tabUpcoming).setOnClickListener(v -> {
            Toast.makeText(this, "Showing upcoming appointments...", Toast.LENGTH_SHORT).show();
            // TODO: Switch to upcoming tab
        });

        findViewById(R.id.tabRescheduled).setOnClickListener(v -> {
            Toast.makeText(this, "Showing rescheduled appointments...", Toast.LENGTH_SHORT).show();
            // TODO: Switch to rescheduled tab
        });

        findViewById(R.id.tabCancelled).setOnClickListener(v -> {
            Toast.makeText(this, "Showing cancelled appointments...", Toast.LENGTH_SHORT).show();
            // TODO: Switch to cancelled tab
        });

        // Primary confirm button
        confirmButton.setOnClickListener(v -> {
            Toast.makeText(this, "Confirming appointment...", Toast.LENGTH_SHORT).show();
            // TODO: Implement appointment confirmation
        });
    }

    private void loadUserData() {
        String userName = session.getFullName();
        // TODO: Display user name in summary card if needed
        // TODO: Load user profile image using Glide or similar
        
        // Set default next checkup time
        if (nextCheckupTime != null) {
            nextCheckupTime.setText("Tomorrow 10:30");
        }
    }

    private void loadAppointments() {
        String uid = session.getUid();
        if (uid == null) return;
        
        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onResult(List<Appointment> data) {
                runOnUiThread(() -> {
                    appointments.clear();
                    appointments.addAll(data);
                    // Sort by date and time, newest first
                    appointments.sort((a1, a2) -> {
                        String dateTime1 = a1.getDate() + " " + a1.getTime();
                        String dateTime2 = a2.getDate() + " " + a2.getTime();
                        return dateTime2.compareTo(dateTime1);
                    });
                    updateAppointmentCard();
                });
            }
            
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, 
                    "Error loading appointments: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateAppointmentCard() {
        // Update the upcoming appointment card with the latest appointment
        if (!appointments.isEmpty()) {
            Appointment latestAppointment = appointments.get(0);
            // TODO: Update the appointment card UI with latestAppointment data
            // This would involve updating doctor name, specialty, date, time, etc.
        }
    }

    // Placeholder methods for future implementation
    private void openAppointmentBooking() {
        // TODO: Navigate to appointment booking flow
        Toast.makeText(this, "Appointment booking coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void openHealthMetrics() {
        // TODO: Navigate to detailed health metrics
        Toast.makeText(this, "Detailed health metrics coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void openAddReminder() {
        // TODO: Open add reminder dialog
        Toast.makeText(this, "Add reminder coming soon!", Toast.LENGTH_SHORT).show();
    }
}
