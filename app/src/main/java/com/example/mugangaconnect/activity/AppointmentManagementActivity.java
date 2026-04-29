package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity {

    private AppointmentRepository appointmentRepo;
    private SessionManager session;
    private final List<Appointment> appointments = new ArrayList<>();

    // UI Components
    private TextView userNameText;
    private TextView profileImage;

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
        userNameText = findViewById(R.id.userNameText);
        profileImage = findViewById(R.id.profileImage);
    }

    private void setupClickListeners() {
        // Smart Alert button
        findViewById(R.id.btnScheduleCheckup).setOnClickListener(v -> {
            Toast.makeText(this, "Opening appointment scheduling...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to appointment booking flow
        });

        // Appointment card buttons
        findViewById(R.id.btnRescheduleAppointment).setOnClickListener(v -> {
            Toast.makeText(this, "Opening reschedule options...", Toast.LENGTH_SHORT).show();
            // TODO: Open reschedule dialog
        });

        findViewById(R.id.btnCancelAppointment).setOnClickListener(v -> {
            Toast.makeText(this, "Cancelling appointment...", Toast.LENGTH_SHORT).show();
            // TODO: Cancel appointment logic
        });

        // Health metrics "View All"
        findViewById(R.id.healthMetricsCard).setOnClickListener(v -> {
            Toast.makeText(this, "Opening detailed health metrics...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to detailed health metrics screen
        });

        // Reminders "Add New"
        findViewById(R.id.remindersCard).setOnClickListener(v -> {
            Toast.makeText(this, "Opening add reminder...", Toast.LENGTH_SHORT).show();
            // TODO: Open add reminder dialog
        });
    }

    private void loadUserData() {
        String userName = session.getFullName();
        if (userName != null && !userName.isEmpty() && userNameText != null) {
            userNameText.setText(userName);
        }
        
        // TODO: Load user profile image using Glide or similar
        // if (profileImage != null) {
        //     Glide.with(this).load(userProfileImageUrl).into(profileImage);
        // }
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
