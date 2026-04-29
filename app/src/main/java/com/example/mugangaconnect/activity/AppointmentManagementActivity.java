package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.dialog.SessionSelectionDialog;
import com.example.mugangaconnect.ui.adapter.AppointmentAdapter;
import com.example.mugangaconnect.ui.adapter.DoctorAdapter;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener,
                   DoctorAdapter.OnDoctorSelectedListener {

    private AppointmentRepository appointmentRepo;
    private SessionManager session;

    private AppointmentAdapter appointmentAdapter;
    private DoctorAdapter doctorAdapter;

    private final List<Appointment> appointments = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();

    private String selectedDepartment = "Cardiology";
    private Doctor selectedDoctor;
    private EditText etVisitReason;
    private String selectedDate = "2025-08-01";
    private String selectedTime = "09:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_management);

        session         = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);
        etVisitReason   = findViewById(R.id.et_visit_reason);

        setupDoctorList();
        setupAppointmentList();
        setupDepartmentTabs();
        setupButtons();

        loadAppointments();

        BottomNavHelper.setup(this, BottomNavHelper.Screen.SCHEDULE);
    }

    private void setupDoctorList() {
        RecyclerView rvDoctors = findViewById(R.id.rv_doctors);
        if (rvDoctors == null) return;
        doctorAdapter = new DoctorAdapter(doctors, this);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        rvDoctors.setAdapter(doctorAdapter);
        loadDoctors();
    }

    private RecyclerView rvAppointments;

    private void setupAppointmentList() {
        rvAppointments = findViewById(R.id.rv_appointments);
        if (rvAppointments == null) return;
        appointmentAdapter = new AppointmentAdapter(appointments, this);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvAppointments.setAdapter(appointmentAdapter);
    }

    private void setupDepartmentTabs() {
        int[] deptIds = {R.id.deptCardiology, R.id.deptNeurology, R.id.deptDentistry};
        String[] deptNames = {"Cardiology", "Neurology", "Dentistry"};
        for (int i = 0; i < deptIds.length; i++) {
            TextView tv = findViewById(deptIds[i]);
            if (tv == null) continue;
            String dept = deptNames[i];
            tv.setOnClickListener(v -> {
                selectedDepartment = dept;
                loadDoctors();
                Toast.makeText(this, dept + " selected", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupButtons() {
        if (findViewById(R.id.btn_select_session) != null)
            findViewById(R.id.btn_select_session).setOnClickListener(v -> showSessionSelectionDialog());

        if (findViewById(R.id.btn_book_appointment) != null)
            findViewById(R.id.btn_book_appointment).setOnClickListener(v -> bookAppointment());
    }

    private void loadDoctors() {
        // Seed static doctors per department — replace with Firestore fetch when available
        doctors.clear();
        doctors.add(new Doctor("d1", "Dr. Mugisha Eric", "Cardiologist", selectedDepartment, "Mon-Fri 08:00-17:00"));
        doctors.add(new Doctor("d2", "Dr. Uwase Claire", "Specialist", selectedDepartment, "Mon-Wed 09:00-15:00"));
        if (doctorAdapter != null) doctorAdapter.notifyDataSetChanged();
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
                    if (appointmentAdapter != null) {
                        appointmentAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void bookAppointment() {
        if (selectedDoctor == null) {
            Toast.makeText(this, "Please select a doctor first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String uid = session.getUid();
        if (uid == null) {
            Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = etVisitReason != null ? etVisitReason.getText().toString().trim() : "";
        if (reason.isEmpty()) {
            Toast.makeText(this, "Please provide a reason for your visit", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that session has been selected
        if ("2025-08-01".equals(selectedDate) && "09:00".equals(selectedTime)) {
            Toast.makeText(this, "Please select a session time using the 'Select Session' button", Toast.LENGTH_LONG).show();
            return;
        }

        // Disable button to prevent double booking
        if (findViewById(R.id.btn_book_appointment) != null) {
            findViewById(R.id.btn_book_appointment).setEnabled(false);
        }

        Appointment appt = new Appointment(uid, selectedDoctor.getId(),
                selectedDoctor.getName(), selectedDepartment,
                selectedDate, selectedTime);
        appt.setReason(reason);

        appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
            @Override
            public void onResult(Appointment data) {
                runOnUiThread(() -> {
                    appointments.add(0, data); // Add at beginning to show newest first
                    if (appointmentAdapter != null) {
                        appointmentAdapter.notifyItemInserted(0);
                        rvAppointments.smoothScrollToPosition(0);
                    }
                    if (etVisitReason != null) etVisitReason.setText("");
                    Toast.makeText(AppointmentManagementActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Re-enable the book button
                    if (findViewById(R.id.btn_book_appointment) != null) {
                        findViewById(R.id.btn_book_appointment).setEnabled(true);
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this, "Failed to book appointment: " + message, Toast.LENGTH_LONG).show();
                    
                    // Re-enable the book button
                    if (findViewById(R.id.btn_book_appointment) != null) {
                        findViewById(R.id.btn_book_appointment).setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        selectedDoctor = doctor;
    }

    @Override
    public void onReschedule(Appointment appointment) {
        appointmentRepo.reschedule(appointment.getId(), "2025-08-10", "10:00",
                new AppointmentRepository.Callback<Void>() {
                    @Override public void onResult(Void data) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, "Rescheduled", Toast.LENGTH_SHORT).show());
                        loadAppointments();
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    @Override
    public void onCancel(Appointment appointment) {
        appointmentRepo.updateStatus(appointment.getId(), session.getUid(),
                Appointment.Status.CANCELLED.name(),
                new AppointmentRepository.Callback<Void>() {
                    @Override public void onResult(Void data) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show());
                        loadAppointments();
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void showSessionSelectionDialog() {
        if (selectedDoctor == null) {
            Toast.makeText(this, "Please select a doctor first", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionSelectionDialog dialog = new SessionSelectionDialog();
        dialog.setOnSessionSelectedListener((date, time) -> {
            selectedDate = date;
            selectedTime = time;
            Toast.makeText(this, "Session selected: " + date + " at " + time, Toast.LENGTH_SHORT).show();
        });
        
        FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, "session_selection_dialog");
    }
}
