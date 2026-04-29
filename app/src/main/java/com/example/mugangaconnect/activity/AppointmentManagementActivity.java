package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.CheckBox;
import android.widget.LinearLayout;

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

    // UI Components - Updated for production-level design
    private TextView nextCheckupTime;
    private EditText searchEditText;
    private MaterialButton confirmButton;
    private RecyclerView doctorRecyclerView;
    private RecyclerView timeSlotRecyclerView;
    private RecyclerView appointmentRecyclerView;
    
    // Step Flow Components
    private LinearLayout doctorSection;
    private LinearLayout timeSlotSection;
    private View doctorStepCircle;
    private View timeStepCircle;
    private View confirmStepCircle;
    private TextView aiRecommendationText;
    
    // Current booking step
    private int currentStep = 1; // 1: Department, 2: Doctor, 3: Time, 4: Confirm

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
        
        // Step flow components
        doctorSection = findViewById(R.id.doctorSection);
        timeSlotSection = findViewById(R.id.timeSlotSection);
        doctorStepCircle = findViewById(R.id.doctorStepCircle);
        timeStepCircle = findViewById(R.id.timeStepCircle);
        confirmStepCircle = findViewById(R.id.confirmStepCircle);
        aiRecommendationText = findViewById(R.id.aiRecommendationText);
    }

    private void setupClickListeners() {
        // Search functionality
        searchEditText.setOnClickListener(v -> {
            Toast.makeText(this, "Opening search...", Toast.LENGTH_SHORT).show();
            // TODO: Implement search functionality
        });

        // Department selection buttons with step flow
        findViewById(R.id.btnCardiology).setOnClickListener(v -> {
            selectDepartment("Cardiology");
        });

        findViewById(R.id.btnNeurology).setOnClickListener(v -> {
            selectDepartment("Neurology");
        });

        findViewById(R.id.btnDentistry).setOnClickListener(v -> {
            selectDepartment("Dentistry");
        });

        findViewById(R.id.btnPediatrics).setOnClickListener(v -> {
            selectDepartment("Pediatrics");
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

    // Step-based booking flow methods
    private void selectDepartment(String department) {
        // Update UI to show department selection
        Toast.makeText(this, "Selected: " + department, Toast.LENGTH_SHORT).show();
        
        // Move to step 2: Show doctor selection
        moveToStep(2);
        
        // TODO: Load doctors for selected department
        loadDoctorsForDepartment(department);
    }
    
    private void moveToStep(int step) {
        currentStep = step;
        
        // Update step indicators
        updateStepIndicators();
        
        // Show/hide appropriate sections
        switch (step) {
            case 2:
                if (doctorSection != null) {
                    doctorSection.setVisibility(android.view.View.VISIBLE);
                }
                if (timeSlotSection != null) {
                    timeSlotSection.setVisibility(android.view.View.GONE);
                }
                break;
            case 3:
                if (doctorSection != null) {
                    doctorSection.setVisibility(android.view.View.VISIBLE);
                }
                if (timeSlotSection != null) {
                    timeSlotSection.setVisibility(android.view.View.VISIBLE);
                }
                break;
            case 4:
                // Show confirmation state
                if (confirmButton != null) {
                    confirmButton.setText("Confirm Appointment");
                }
                break;
        }
    }
    
    private void updateStepIndicators() {
        // Update step circles based on current step
        if (doctorStepCircle != null) {
            doctorStepCircle.setBackgroundResource(currentStep >= 2 ? 
                R.drawable.step_completed_circle : R.drawable.step_inactive_circle);
        }
        
        if (timeStepCircle != null) {
            timeStepCircle.setBackgroundResource(currentStep >= 3 ? 
                R.drawable.step_completed_circle : R.drawable.step_inactive_circle);
        }
        
        if (confirmStepCircle != null) {
            confirmStepCircle.setBackgroundResource(currentStep >= 4 ? 
                R.drawable.step_active_circle : R.drawable.step_inactive_circle);
        }
    }
    
    private void loadDoctorsForDepartment(String department) {
        // TODO: Implement doctor loading logic
        // This would typically involve:
        // 1. Querying database/API for doctors in selected department
        // 2. Setting up RecyclerView adapter
        // 3. Populating the doctor list
        
        // For now, show a placeholder message
        if (aiRecommendationText != null) {
            aiRecommendationText.setText("Loading doctors for " + department + "...");
        }
    }
    
    private void selectTimeSlot(String timeSlot) {
        // Move to step 3: Show time selection
        moveToStep(3);
        
        // TODO: Handle time slot selection
        Toast.makeText(this, "Selected time: " + timeSlot, Toast.LENGTH_SHORT).show();
        
        // Show AI recommendation
        if (aiRecommendationText != null) {
            aiRecommendationText.setText("Best time: " + timeSlot + " • Low no-show risk");
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
