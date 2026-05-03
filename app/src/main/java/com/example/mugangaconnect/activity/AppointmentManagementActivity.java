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

    // UI Components - Updated for premium design
    private TextView nextCheckupTime;
    private EditText searchEditText;
    private MaterialButton bookAppointmentButton;
    private RecyclerView departmentRecyclerView;
    private RecyclerView specialistRecyclerView;
    private RecyclerView timeSlotRecyclerView;
    private RecyclerView appointmentRecyclerView;
    
    // Step Flow Components
    private LinearLayout specialistSection;
    private LinearLayout timeSlotSection;
    private View specialistStepCircle;
    private View timeSlotStepCircle;
    
    // Date selector buttons
    private MaterialButton dateApr24;
    private MaterialButton dateApr25;
    private MaterialButton dateApr26;
    
    // Current booking step
    private int currentStep = 1; // 1: Department, 2: Specialist, 3: Time, 4: Confirm

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
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        departmentRecyclerView = findViewById(R.id.departmentRecyclerView);
        specialistRecyclerView = findViewById(R.id.specialistRecyclerView);
        timeSlotRecyclerView = findViewById(R.id.timeSlotRecyclerView);
        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView);
        
        // Step flow components
        specialistSection = findViewById(R.id.specialistSection);
        timeSlotSection = findViewById(R.id.timeSlotSection);
        specialistStepCircle = findViewById(R.id.specialistStepCircle);
        timeSlotStepCircle = findViewById(R.id.timeSlotStepCircle);
        
        // Date selector buttons
        dateApr24 = findViewById(R.id.dateApr24);
        dateApr25 = findViewById(R.id.dateApr25);
        dateApr26 = findViewById(R.id.dateApr26);
    }

    private void setupClickListeners() {
        // Search functionality
        searchEditText.setOnClickListener(v -> {
            Toast.makeText(this, "Opening search...", Toast.LENGTH_SHORT).show();
            // TODO: Implement search functionality
        });

        // Hospital chips
        findViewById(R.id.chipKingFaisal).setOnClickListener(v -> {
            Toast.makeText(this, "Selected: King Faisal Hospital", Toast.LENGTH_SHORT).show();
            // TODO: Filter by hospital
        });

        findViewById(R.id.chipRwandaMilitary).setOnClickListener(v -> {
            Toast.makeText(this, "Selected: Rwanda Military Hospital", Toast.LENGTH_SHORT).show();
            // TODO: Filter by hospital
        });

        findViewById(R.id.chipCHUK).setOnClickListener(v -> {
            Toast.makeText(this, "Selected: CHUK Hospital", Toast.LENGTH_SHORT).show();
            // TODO: Filter by hospital
        });

        // Date selector buttons
        if (dateApr24 != null) {
            dateApr24.setOnClickListener(v -> selectDate("Apr 24"));
        }
        if (dateApr25 != null) {
            dateApr25.setOnClickListener(v -> selectDate("Apr 25"));
        }
        if (dateApr26 != null) {
            dateApr26.setOnClickListener(v -> selectDate("Apr 26"));
        }

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

        // Primary book appointment button
        if (bookAppointmentButton != null) {
            bookAppointmentButton.setOnClickListener(v -> {
                Toast.makeText(this, "Booking appointment...", Toast.LENGTH_SHORT).show();
                // TODO: Implement appointment booking
            });
        }

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

        // Primary book appointment button
        if (bookAppointmentButton != null) {
            bookAppointmentButton.setOnClickListener(v -> {
                Toast.makeText(this, "Booking appointment...", Toast.LENGTH_SHORT).show();
                // TODO: Implement appointment booking
            });
        }
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

    // Step-based booking flow methods for premium design
    private void selectDepartment(String department) {
        // Update UI to show department selection
        Toast.makeText(this, "Selected: " + department, Toast.LENGTH_SHORT).show();
        
        // Move to step 2: Show specialist selection
        moveToStep(2);
        
        // TODO: Load specialists for selected department
        loadSpecialistsForDepartment(department);
    }
    
    private void selectDate(String date) {
        // Update date button states
        updateDateButtons(date);
        
        // Move to step 3: Show time slot selection
        moveToStep(3);
        
        // TODO: Load time slots for selected date
        loadTimeSlotsForDate(date);
    }
    
    private void updateDateButtons(String selectedDate) {
        // Reset all date buttons to default state
        if (dateApr24 != null) {
            dateApr24.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            dateApr24.setStrokeColorResource(android.R.color.darker_gray);
            dateApr24.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        if (dateApr25 != null) {
            dateApr25.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            dateApr25.setStrokeColorResource(android.R.color.darker_gray);
            dateApr25.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        if (dateApr26 != null) {
            dateApr26.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            dateApr26.setStrokeColorResource(android.R.color.darker_gray);
            dateApr26.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        // Highlight selected date
        if ("Apr 24".equals(selectedDate) && dateApr24 != null) {
            dateApr24.setBackgroundTintList(getResources().getColorStateList(R.color.primary_blue));
            dateApr24.setTextColor(getResources().getColor(android.R.color.white));
        } else if ("Apr 25".equals(selectedDate) && dateApr25 != null) {
            dateApr25.setBackgroundTintList(getResources().getColorStateList(R.color.primary_blue));
            dateApr25.setTextColor(getResources().getColor(android.R.color.white));
        } else if ("Apr 26".equals(selectedDate) && dateApr26 != null) {
            dateApr26.setBackgroundTintList(getResources().getColorStateList(R.color.primary_blue));
            dateApr26.setTextColor(getResources().getColor(android.R.color.white));
        }
    }
    
    private void moveToStep(int step) {
        currentStep = step;
        
        // Update step indicators
        updateStepIndicators();
        
        // Show/hide appropriate sections
        switch (step) {
            case 2:
                if (specialistSection != null) {
                    specialistSection.setVisibility(android.view.View.VISIBLE);
                }
                if (timeSlotSection != null) {
                    timeSlotSection.setVisibility(android.view.View.GONE);
                }
                break;
            case 3:
                if (specialistSection != null) {
                    specialistSection.setVisibility(android.view.View.VISIBLE);
                }
                if (timeSlotSection != null) {
                    timeSlotSection.setVisibility(android.view.View.VISIBLE);
                }
                break;
            case 4:
                // Show AI recommendation
                findViewById(R.id.aiRecommendationCard).setVisibility(android.view.View.VISIBLE);
                break;
        }
    }
    
    private void updateStepIndicators() {
        // Update step circles based on current step
        if (specialistStepCircle != null) {
            specialistStepCircle.setBackgroundResource(currentStep >= 2 ? 
                R.drawable.step_completed_circle : R.drawable.step_inactive_circle);
        }
        
        if (timeSlotStepCircle != null) {
            timeSlotStepCircle.setBackgroundResource(currentStep >= 3 ? 
                R.drawable.step_completed_circle : R.drawable.step_inactive_circle);
        }
    }
    
    private void loadSpecialistsForDepartment(String department) {
        // TODO: Implement specialist loading logic
        // This would typically involve:
        // 1. Querying database/API for specialists in selected department
        // 2. Setting up RecyclerView adapter
        // 3. Populating the specialist list
        
        // For now, show a placeholder message
        Toast.makeText(this, "Loading specialists for " + department + "...", Toast.LENGTH_SHORT).show();
    }
    
    private void loadTimeSlotsForDate(String date) {
        // TODO: Implement time slot loading logic
        // This would typically involve:
        // 1. Querying database/API for available time slots
        // 2. Setting up RecyclerView adapter with grid layout
        // 3. Populating time slots with proper states (available, booked, selected)
        
        // Show AI recommendation card
        findViewById(R.id.aiRecommendationCard).setVisibility(android.view.View.VISIBLE);
    }
    
    private void selectTimeSlot(String timeSlot) {
        // TODO: Handle time slot selection
        Toast.makeText(this, "Selected time: " + timeSlot, Toast.LENGTH_SHORT).show();
        
        // Move to confirmation step
        moveToStep(4);
        
        // Show AI recommendation card
        findViewById(R.id.aiRecommendationCard).setVisibility(android.view.View.VISIBLE);
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
