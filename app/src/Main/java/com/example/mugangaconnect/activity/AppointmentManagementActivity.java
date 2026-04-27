package com.example.mugangaconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.data.repository.DoctorRepository;
import com.example.mugangaconnect.ui.adapter.AppointmentAdapter;
import com.example.mugangaconnect.ui.adapter.DoctorAdapter;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener,
                   DoctorAdapter.OnDoctorSelectedListener {

    private AppointmentRepository appointmentRepo;
    private DoctorRepository doctorRepo;
    private SessionManager session;

    private AppointmentAdapter appointmentAdapter;
    private DoctorAdapter doctorAdapter;

    private final List<Appointment> appointments = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();

    private String selectedDepartment = "Cardiology";
    private Doctor selectedDoctor;
    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_management);

        session         = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);
        doctorRepo      = new DoctorRepository();

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

    private void setupAppointmentList() {
        RecyclerView rvAppointments = findViewById(R.id.rv_appointments);
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
            findViewById(R.id.btn_select_session).setOnClickListener(v -> showDatePicker());

        if (findViewById(R.id.btn_book_appointment) != null)
            findViewById(R.id.btn_book_appointment).setOnClickListener(v -> bookAppointment());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Format date as YYYY-MM-DD
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    showTimePicker();
                },
                2025, 8, 1 // Default date: August 1, 2025
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Format time as HH:MM
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    Toast.makeText(this, "Selected: " + selectedDate + " " + selectedTime, Toast.LENGTH_SHORT).show();
                },
                9, 0 // Default time: 09:00
        );
        timePickerDialog.show();
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
                    if (appointmentAdapter != null) appointmentAdapter.notifyDataSetChanged();
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
        
        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedTime == null || selectedTime.trim().isEmpty()) {
            Toast.makeText(this, "Please select a time first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String uid = session.getUid();
        if (uid == null) return;

        Appointment appt = new Appointment(uid, selectedDoctor.getId(),
                selectedDoctor.getName(), selectedDepartment,
                selectedDate, selectedTime);

        appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
            @Override
            public void onResult(Appointment data) {
                runOnUiThread(() -> {
                    appointments.add(data);
                    if (appointmentAdapter != null) appointmentAdapter.notifyItemInserted(appointments.size() - 1);
                    Toast.makeText(AppointmentManagementActivity.this, "Appointment booked!", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
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
}
