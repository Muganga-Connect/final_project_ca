package com.example.mugangaconnect.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.data.repository.DoctorRepository;
import com.example.mugangaconnect.ui.adapter.DoctorAdapter;
import com.example.mugangaconnect.ui.adapter.HospitalAdapter;
import com.example.mugangaconnect.data.model.Hospital;
import com.example.mugangaconnect.data.repository.HospitalRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentBookingActivity extends AppCompatActivity
        implements DoctorAdapter.OnDoctorSelectedListener {

    private AppointmentRepository appointmentRepo;
    private DoctorRepository      doctorRepo;
    private HospitalRepository    hospitalRepo;
    private SessionManager        session;

    private Hospital selectedHospital;
    private String selectedDepartment = "General";
    private Doctor selectedDoctor;
    private String selectedTimeSlot = "10:30 AM";
    private String selectedDate;
    private String rescheduleId;
    private final List<Doctor> doctors = new ArrayList<>();

    private DoctorAdapter doctorAdapter;
    private HospitalAdapter hospitalAdapter;
    private RecyclerView  rvDoctors;
    private RecyclerView  rvHospitals;
    private final List<Hospital> hospitals = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_management_booking);

        session         = new SessionManager(this);
        appointmentRepo = new AppointmentRepository();
        doctorRepo      = new DoctorRepository();
        hospitalRepo    = new HospitalRepository();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        rescheduleId = getIntent().getStringExtra("reschedule_id");
        if (rescheduleId != null) {
            selectedDepartment = getIntent().getStringExtra("department");
            // Optionally set other fields if provided
        }

        setupHeader();
        setupSpecializationChips();
        setupHospitalSelection();
        setupDoctorSelection();
        setupDateTimeSelection();
        setupBookButton();

        loadHospitals();
    }

    private void setupHospitalSelection() {
        rvHospitals = findViewById(R.id.rvHospitals);
        if (rvHospitals != null) {
            hospitalAdapter = new HospitalAdapter(hospitals, hospital -> {
                selectedHospital = hospital;
                loadDoctors(selectedDepartment);
            });
            rvHospitals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvHospitals.setAdapter(hospitalAdapter);
        }
    }

    private void loadHospitals() {
        hospitalRepo.getAll(new HospitalRepository.Callback<List<Hospital>>() {
            @Override public void onSuccess(List<Hospital> result) {
                runOnUiThread(() -> {
                    hospitals.clear();
                    hospitals.addAll(result);
                    if (hospitalAdapter != null) hospitalAdapter.notifyDataSetChanged();
                });
            }
            @Override public void onError(String error) {}
        });
    }

    private void setupHeader() {
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupSpecializationChips() {
        // IDs from the layout: usually they would be set in the HorizontalScrollView chips
        // For now, we'll use a simple approach if IDs were added, but we'll stick to the logic
    }

    private void setupDoctorSelection() {
        rvDoctors = findViewById(R.id.rvDoctors);
        if (rvDoctors != null) {
            doctorAdapter = new DoctorAdapter(doctors, this);
            rvDoctors.setLayoutManager(new LinearLayoutManager(this));
            rvDoctors.setAdapter(doctorAdapter);
        }
    }

    private void loadDoctors(String department) {
        if (selectedHospital == null) return;
        
        doctorRepo.getByHospital(selectedHospital.getId(), new DoctorRepository.Callback<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> result) {
                runOnUiThread(() -> {
                    doctors.clear();
                    // Filter by department if needed
                    for (Doctor d : result) {
                        if (d.getDepartment().equalsIgnoreCase(department)) {
                            doctors.add(d);
                        }
                    }
                    if (doctorAdapter != null) doctorAdapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        selectedDoctor = doctor;
        Toast.makeText(this, "Selected: " + doctor.getName(), Toast.LENGTH_SHORT).show();
    }

    private void setupDateTimeSelection() {
        // rvTimeSlots and calendarView setup
    }

    private void setupBookButton() {
        View btnBook = findViewById(R.id.btnBookNow);
        if (btnBook != null) btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment() {
        if (selectedDoctor == null && rescheduleId == null)  { Toast.makeText(this, "Please select a doctor first", Toast.LENGTH_SHORT).show(); return; }
        String uid = session.getUid();
        if (uid == null) { Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return; }

        findViewById(R.id.btnBookNow).setEnabled(false);

        if (rescheduleId != null) {
            appointmentRepo.reschedule(rescheduleId, selectedDate, selectedTimeSlot, new AppointmentRepository.Callback<Void>() {
                @Override public void onSuccess(Void result) {
                    runOnUiThread(() -> {
                        Toast.makeText(AppointmentBookingActivity.this, "Rescheduled Successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
                @Override public void onError(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(AppointmentBookingActivity.this, "Reschedule failed: " + message, Toast.LENGTH_LONG).show();
                        findViewById(R.id.btnBookNow).setEnabled(true);
                    });
                }
            });
        } else {
            String patientName = session.getName(); 
            if (patientName == null || patientName.isEmpty()) patientName = "Anonymous Patient";

            Appointment appt = new Appointment(uid, selectedDoctor.getId(), selectedDoctor.getName(), selectedDepartment, selectedDate, selectedTimeSlot);
            appt.setPatientId(uid);
            appt.setPatientName(patientName);
            
            if (selectedHospital != null) {
                appt.setHospitalId(selectedHospital.getId());
                appt.setHospitalName(selectedHospital.getName());
            }
            appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
                @Override public void onSuccess(Appointment result) {
                    runOnUiThread(() -> {
                        Toast.makeText(AppointmentBookingActivity.this, "Booked Successfully!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AppointmentBookingActivity.this, AppointmentHistoryActivity.class));
                        finish();
                    });
                }
                @Override public void onError(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(AppointmentBookingActivity.this, "Booking failed: " + message, Toast.LENGTH_LONG).show();
                        findViewById(R.id.btnBookNow).setEnabled(true);
                    });
                }
            });
        }
    }
}
