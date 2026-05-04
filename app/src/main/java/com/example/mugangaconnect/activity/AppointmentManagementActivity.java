package com.example.mugangaconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
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
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentManagementActivity extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener,
                   DoctorAdapter.OnDoctorSelectedListener {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    private AppointmentRepository appointmentRepo;
    private DoctorRepository doctorRepo;
    private SessionManager session;

    private AppointmentAdapter appointmentAdapter;
    private DoctorAdapter doctorAdapter;

    private final List<Appointment> appointments = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();

    private String selectedDepartment = "Cardiology";
    private Doctor selectedDoctor;
    private Calendar selectedDateTime = Calendar.getInstance();
    private boolean isDateTimeSelected = false;

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
        loadDoctors(); // Initial load

        com.example.mugangaconnect.BottomNavHelper.setup(this, com.example.mugangaconnect.BottomNavHelper.Screen.SCHEDULE);
    }

    private void setupDoctorList() {
        RecyclerView rvDoctors = findViewById(R.id.rv_doctors);
        if (rvDoctors == null) return;
        doctorAdapter = new DoctorAdapter(doctors, this);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDoctors.setAdapter(doctorAdapter);
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
            final String dept = deptNames[i];
            tv.setOnClickListener(v -> {
                selectedDepartment = dept;
                updateTabUI(v);
                loadDoctors();
            });
        }
    }

    private void updateTabUI(View selectedTab) {
        int[] deptIds = {R.id.deptCardiology, R.id.deptNeurology, R.id.deptDentistry};
        for (int id : deptIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setBackgroundResource(id == selectedTab.getId() ? R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
                tv.setTextColor(id == selectedTab.getId() ? getColor(android.R.color.white) : getColor(android.R.color.black));
            }
        }
    }

    private void setupButtons() {
        View btnSelectSession = findViewById(R.id.btn_select_session);
        if (btnSelectSession != null) {
            btnSelectSession.setOnClickListener(v -> showDateTimePicker(null));
        }

        View btnBook = findViewById(R.id.btn_book_appointment);
        if (btnBook != null) {
            btnBook.setOnClickListener(v -> bookAppointment());
        }
    }

    private void showDateTimePicker(Appointment apptToReschedule) {
        final Calendar current = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePicker = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                isDateTimeSelected = true;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formatted = sdf.format(selectedDateTime.getTime());
                
                if (apptToReschedule != null) {
                    processReschedule(apptToReschedule, formatted.split(" ")[0], formatted.split(" ")[1]);
                } else {
                    Toast.makeText(this, "Selected: " + formatted, Toast.LENGTH_SHORT).show();
                    TextView btnText = findViewById(R.id.btn_select_session);
                    if (btnText != null) ((TextView) btnText).setText("Session: " + formatted);
                }
            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true);
            timePicker.show();
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void loadDoctors() {
        doctorRepo.getByDepartment(selectedDepartment, new DoctorRepository.Callback<List<Doctor>>() {
            @Override
            public void onResult(List<Doctor> data) {
                runOnUiThread(() -> {
                    doctors.clear();
                    if (data.isEmpty()) {
                        doctors.add(new Doctor("d1", "Dr. Mugisha Eric", "Cardiologist", selectedDepartment, "Mon-Fri 08:00-17:00"));
                        doctors.add(new Doctor("d2", "Dr. Uwase Claire", "Specialist", selectedDepartment, "Mon-Wed 09:00-15:00"));
                    } else {
                        doctors.addAll(data);
                    }
                    if (doctorAdapter != null) doctorAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
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
        if (!isDateTimeSelected) {
            Toast.makeText(this, "Please select a session time", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = session.getUid();
        if (uid == null) return;

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Appointment appt = new Appointment(uid, selectedDoctor.getId(),
                selectedDoctor.getName(), selectedDepartment,
                dateSdf.format(selectedDateTime.getTime()), 
                timeSdf.format(selectedDateTime.getTime()));

        appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
            @Override
            public void onResult(Appointment data) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this, "Appointment booked!", Toast.LENGTH_SHORT).show();
                    isDateTimeSelected = false;
                    TextView btnText = findViewById(R.id.btn_select_session);
                    if (btnText != null) ((TextView) btnText).setText("Select Session");
                    loadAppointments();
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
        Toast.makeText(this, "Selected: " + doctor.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReschedule(Appointment appointment) {
        showDateTimePicker(appointment);
    }

    private void processReschedule(Appointment appointment, String newDate, String newTime) {
        appointmentRepo.reschedule(appointment.getId(), newDate, newTime,
                new AppointmentRepository.Callback<Void>() {
                    @Override public void onResult(Void data) {
                        runOnUiThread(() -> {
                            Toast.makeText(AppointmentManagementActivity.this, "Rescheduled to " + newDate + " " + newTime, Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        });
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
                        runOnUiThread(() -> {
                            Toast.makeText(AppointmentManagementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        });
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
