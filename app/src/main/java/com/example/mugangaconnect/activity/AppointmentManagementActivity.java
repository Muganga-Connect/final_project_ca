package com.example.mugangaconnect.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.data.repository.DoctorRepository;
import com.example.mugangaconnect.ui.adapter.DoctorAdapter;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentManagementActivity extends AppCompatActivity
        implements DoctorAdapter.OnDoctorSelectedListener {

    private AppointmentRepository appointmentRepo;
    private DoctorRepository      doctorRepo;
    private SessionManager        session;

    private String selectedDepartment = "Cardiology";
    private Doctor selectedDoctor;
    private String selectedTimeSlot;
    private String selectedDate;
    private final List<Doctor> doctors = new ArrayList<>();

    private DoctorAdapter doctorAdapter;
    private RecyclerView  rvDoctors;

    private final int[]     slotIds      = {};
    private final String[]  slotTimes    = {"09:00","09:30","10:00","10:30","11:00","11:30","13:00","13:30","14:00"};
    private final boolean[] slotAvailable= {true,true,false,true,true,true,true,true,false};

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_management);

        session         = new SessionManager(this);
        appointmentRepo = new AppointmentRepository();
        doctorRepo      = new DoctorRepository();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        setupMenuAndViewAll();
        setupHospitalChips();
        setupSearch();
        setupDepartmentSelection();
        setupTimeSlots();
        setupDayPicker();
        setupBookButton();
        setupChooseAnotherDoctor();

        BottomNavHelper.setup(this, BottomNavHelper.Screen.SCHEDULE);
        loadDoctors(selectedDepartment);
    }

    private void setupMenuAndViewAll() {
        // ivMenu and tvViewAllHospitals not in current layout — skip
    }

    private void setupHospitalChips() {
        View llCity    = findViewById(R.id.hospitalCityGeneral);
        View llMayo    = findViewById(R.id.hospitalMayo);
        View llStMarys = findViewById(R.id.hospitalStMarys);
        if (llCity    != null) llCity.setOnClickListener(v -> onHospitalSelected("City General Hospital"));
        if (llMayo    != null) llMayo.setOnClickListener(v -> onHospitalSelected("Mayo Clinic"));
        if (llStMarys != null) llStMarys.setOnClickListener(v -> onHospitalSelected("St. Mary's Hospital"));
    }

    private void onHospitalSelected(String name) {
        Toast.makeText(this, name + " selected", Toast.LENGTH_SHORT).show();
        loadDoctors(selectedDepartment);
    }

    private void setupSearch() {
        // etSearch not in current layout — skip
    }

    private void setupDepartmentSelection() {
        View deptCardiology = findViewById(R.id.deptCardiology);
        View deptDentistry  = findViewById(R.id.deptDentistry);
        View deptNeurology  = findViewById(R.id.deptNeurology);
        if (deptCardiology != null) deptCardiology.setOnClickListener(v -> { selectedDepartment = "Cardiology"; loadDoctors(selectedDepartment); });
        if (deptDentistry  != null) deptDentistry.setOnClickListener(v  -> { selectedDepartment = "Dentistry";  loadDoctors(selectedDepartment); });
        if (deptNeurology  != null) deptNeurology.setOnClickListener(v  -> { selectedDepartment = "Neurology";  loadDoctors(selectedDepartment); });
    }

    private void setupChooseAnotherDoctor() {
        rvDoctors = findViewById(R.id.rv_doctors);
        if (rvDoctors != null) {
            doctorAdapter = new DoctorAdapter(doctors, this);
            rvDoctors.setLayoutManager(new LinearLayoutManager(this));
            rvDoctors.setAdapter(doctorAdapter);
        }
    }

    private void loadDoctors(String department) {
        doctorRepo.getByDepartment(department, new DoctorRepository.Callback<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> result) {
                runOnUiThread(() -> {
                    doctors.clear();
                    doctors.addAll(result);
                    if (!doctors.isEmpty()) {
                        selectedDoctor = doctors.get(0);
                        updateDoctorCard(selectedDoctor);
                    }
                    if (doctorAdapter != null) doctorAdapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void updateDoctorCard(Doctor doctor) {
        TextView tvName = findViewById(R.id.tvDoctorName);
        TextView tvDept = findViewById(R.id.tvDoctorDept);
        if (tvName != null) tvName.setText(doctor.getName());
        if (tvDept != null) tvDept.setText(doctor.getSpecialty());
    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        selectedDoctor = doctor;
        updateDoctorCard(doctor);
        if (rvDoctors != null) rvDoctors.setVisibility(View.GONE);
        Toast.makeText(this, doctor.getName(), Toast.LENGTH_SHORT).show();
    }

    private void setupTimeSlots() {
        // Time slots not in current layout — default selection only
        selectedTimeSlot = slotTimes.length > 3 ? slotTimes[3] : null;
    }

    private void selectTimeSlot(String time) {
        selectedTimeSlot = time;
    }

    private void setupDayPicker() {
        View btnSelectSession = findViewById(R.id.btn_select_session);
        if (btnSelectSession != null) btnSelectSession.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            c.set(year, month, day);
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime());
            Toast.makeText(this, "Date: " + selectedDate, Toast.LENGTH_SHORT).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupBookButton() {
        View btnBook = findViewById(R.id.btn_book_appointment);
        if (btnBook != null) btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment() {
        if (selectedDoctor == null)  { Toast.makeText(this, "Please select a doctor first", Toast.LENGTH_SHORT).show(); return; }
        if (selectedTimeSlot == null){ Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show(); return; }
        String uid = session.getUid();
        if (uid == null) { Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return; }

        Appointment appt = new Appointment(uid, selectedDoctor.getId(), selectedDoctor.getName(), selectedDepartment, selectedDate, selectedTimeSlot);
        View bookBtn = findViewById(R.id.btn_book_appointment);
        if (bookBtn != null) bookBtn.setEnabled(false);

        appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
            @Override public void onSuccess(Appointment result) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this, "Booked with " + selectedDoctor.getName() + " on " + selectedDate + " at " + selectedTimeSlot, Toast.LENGTH_LONG).show();
                    if (bookBtn != null) bookBtn.setEnabled(true);
                    startActivity(new Intent(AppointmentManagementActivity.this, AppointmentHistoryActivity.class));
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> { Toast.makeText(AppointmentManagementActivity.this, "Booking failed: " + message, Toast.LENGTH_LONG).show(); if (bookBtn != null) bookBtn.setEnabled(true); });
            }
        });
    }

    public void rescheduleAppointment(Appointment appointment) {
        appointmentRepo.reschedule(appointment.getId(), selectedDate, selectedTimeSlot,
                new AppointmentRepository.Callback<Void>() {
                    @Override public void onSuccess(Void result) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this,
                                getString(R.string.reschedule_appointment) + ": " + selectedDate + " " + selectedTimeSlot,
                                Toast.LENGTH_SHORT).show());
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    public void cancelAppointment(Appointment appointment) {
        appointmentRepo.updateStatus(appointment.getId(), session.getUid(),
                Appointment.Status.CANCELLED.name(),
                new AppointmentRepository.Callback<Void>() {
                    @Override public void onSuccess(Void result) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this,
                                getString(R.string.cancelled), Toast.LENGTH_SHORT).show());
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
