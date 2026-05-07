package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppointmentManagementActivity extends AppCompatActivity {

    private AppointmentRepository appointmentRepo;
    private SessionManager session;
    private final List<Appointment> appointments = new ArrayList<>();

    private TextView nextCheckupTime;
    private EditText searchEditText;
    private Button bookAppointmentButton;

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
    }

    private void initializeViews() {
        nextCheckupTime = findViewById(R.id.tvNextCheckup);
        searchEditText = findViewById(R.id.etSearch);
        bookAppointmentButton = findViewById(R.id.btnBookAppointment);
    }

    private void setupClickListeners() {
        if (searchEditText != null) {
            searchEditText.setOnClickListener(v ->
                Toast.makeText(this, "Opening search...", Toast.LENGTH_SHORT).show());
        }

        if (findViewById(R.id.llCardiology) != null)
            findViewById(R.id.llCardiology).setOnClickListener(v -> selectDepartment("Cardiology"));
        if (findViewById(R.id.llNeurology) != null)
            findViewById(R.id.llNeurology).setOnClickListener(v -> selectDepartment("Neurology"));
        if (findViewById(R.id.llDentistry) != null)
            findViewById(R.id.llDentistry).setOnClickListener(v -> selectDepartment("Dentistry"));

        if (bookAppointmentButton != null) {
            bookAppointmentButton.setOnClickListener(v ->
                Toast.makeText(this, "Booking appointment...", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadUserData() {
        if (nextCheckupTime != null) {
            nextCheckupTime.setText("Tomorrow, 10:30 AM");
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
                    appointments.sort(AppointmentManagementActivity.this::compareAppointmentsNewestFirst);
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
        if (!appointments.isEmpty()) {
            Appointment latest = appointments.get(0);
            // TODO: Update appointment card UI with latest data
        }
    }

    private int compareAppointmentsNewestFirst(Appointment a1, Appointment a2) {
        String dateTime1 = safeDateTime(a1);
        String dateTime2 = safeDateTime(a2);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        formatter.setLenient(false);
        try {
            return formatter.parse(dateTime2).compareTo(formatter.parse(dateTime1));
        } catch (ParseException | NullPointerException e) {
            return dateTime2.compareTo(dateTime1);
        }
    }

    private String safeDateTime(Appointment appointment) {
        if (appointment == null) return "";
        String date = appointment.getDate() == null ? "" : appointment.getDate();
        String time = appointment.getTime() == null ? "" : appointment.getTime();
        return date + " " + time;
    }

    private void selectDepartment(String department) {
        Toast.makeText(this, "Selected: " + department, Toast.LENGTH_SHORT).show();
        // TODO: Load specialists for selected department
    }
}
