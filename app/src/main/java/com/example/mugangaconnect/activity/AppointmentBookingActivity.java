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
                        String docDept = d.getDepartment();
                        if (docDept != null && docDept.equalsIgnoreCase(department)) {
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
        calendarView = findViewById(R.id.calendarView);
        RecyclerView rvTimeSlots = findViewById(R.id.rvTimeSlots);
        
        if (rvTimeSlots != null) {
            rvTimeSlots.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 3));
        }

        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                loadAvailableTimeSlots(selectedDate);
            });
        }
    }

    private void loadAvailableTimeSlots(String date) {
        if (selectedDoctor == null) {
            Toast.makeText(this, "Please select a doctor first", Toast.LENGTH_SHORT).show();
            return;
        }

        appointmentRepo.getBookedSlots(selectedDoctor.getId(), date, new AppointmentRepository.Callback<List<String>>() {
            @Override
            public void onSuccess(List<String> bookedSlots) {
                List<String> allSlots = generateSlotsForDoctor(selectedDoctor);
                runOnUiThread(() -> {
                    TimeSlotAdapter adapter = new TimeSlotAdapter(allSlots, bookedSlots, slot -> {
                        selectedTimeSlot = slot;
                    });
                    RecyclerView rvTimeSlots = findViewById(R.id.rvTimeSlots);
                    if (rvTimeSlots != null) rvTimeSlots.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(AppointmentBookingActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private List<String> generateSlotsForDoctor(Doctor doctor) {
        List<String> slots = new ArrayList<>();
        Doctor.AvailabilitySchedule schedule = doctor.getAvailabilitySchedule();
        
        String start = (schedule != null && schedule.startTime != null) ? schedule.startTime : "08:00";
        String end = (schedule != null && schedule.endTime != null) ? schedule.endTime : "17:00";
        int duration = (schedule != null && schedule.slotDuration > 0) ? schedule.slotDuration : 30;

        try {
            int startHour = Integer.parseInt(start.split(":")[0]);
            int startMin = Integer.parseInt(start.split(":")[1]);
            int endHour = Integer.parseInt(end.split(":")[0]);
            int endMin = Integer.parseInt(end.split(":")[1]);

            int currentTotalMin = startHour * 60 + startMin;
            int endTotalMin = endHour * 60 + endMin;

            while (currentTotalMin + duration <= endTotalMin) {
                int h = currentTotalMin / 60;
                int m = currentTotalMin % 60;
                slots.add(String.format("%02d:%02d", h, m));
                currentTotalMin += duration;
            }
        } catch (Exception e) {
            // Default slots if schedule is malformed
            slots.add("09:00"); slots.add("10:00"); slots.add("11:00");
            slots.add("14:00"); slots.add("15:00"); slots.add("16:00");
        }
        return slots;
    }

    private void setupBookButton() {
        View btnBook = findViewById(R.id.btnConfirmBooking);
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
            String patientName = session.getFullName(); 
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
