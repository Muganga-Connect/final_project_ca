package com.example.mugangaconnect.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.data.repository.DoctorRepository;
import com.example.mugangaconnect.ui.adapter.DoctorAdapter;
import com.example.mugangaconnect.utils.SessionManager;
import com.example.mugangaconnect.activity.BottomNavHelper;

import java.util.ArrayList;
import java.util.List;

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

    private final int[]     slotIds      = {R.id.tvSlot0900, R.id.tvSlot0930, R.id.tvSlot1000, R.id.tvSlot1030, R.id.tvSlot1100, R.id.tvSlot1130, R.id.tvSlot0100, R.id.tvSlot0130, R.id.tvSlot0200};
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
        View ivMenu = findViewById(R.id.ivMenu);
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                String[] options = {"Dashboard", "Appointment History", "AI Assistant", "Profile"};
                new AlertDialog.Builder(this)
                        .setTitle("Navigate to")
                        .setItems(options, (dialog, which) -> {
                            switch (which) {
                                case 0: startActivity(new Intent(this, MainActivity.class)); break;
                                case 1: startActivity(new Intent(this, AppointmentHistoryActivity.class)); break;
                                case 2: startActivity(new Intent(this, AIAssistantActivity.class)); break;
                                case 3: startActivity(new Intent(this, ProfileActivity.class)); break;
                            }
                        }).show();
            });
        }

        TextView tvViewAll = findViewById(R.id.tvViewAllHospitals);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                String[] hospitals = {"City General Hospital","Mayo Clinic","St. Mary's Hospital","King Faisal Hospital","CHUK","CHUB","Kibagabaga Hospital"};
                new AlertDialog.Builder(this)
                        .setTitle("All Hospitals")
                        .setItems(hospitals, (dialog, which) -> {
                            Toast.makeText(this, hospitals[which] + " selected", Toast.LENGTH_SHORT).show();
                            EditText et = findViewById(R.id.etSearch);
                            if (et != null) et.setText(hospitals[which]);
                        }).show();
            });
        }
    }

    private void setupHospitalChips() {
        LinearLayout llCity   = findViewById(R.id.llHospitalCityGeneral);
        LinearLayout llMayo   = findViewById(R.id.llHospitalMayo);
        LinearLayout llStMarys= findViewById(R.id.llHospitalStMarys);
        if (llCity    != null) llCity.setOnClickListener(v -> onHospitalSelected("City General Hospital"));
        if (llMayo    != null) llMayo.setOnClickListener(v -> onHospitalSelected("Mayo Clinic"));
        if (llStMarys != null) llStMarys.setOnClickListener(v -> onHospitalSelected("St. Mary's Hospital"));
    }

    private void onHospitalSelected(String name) {
        Toast.makeText(this, name + " selected", Toast.LENGTH_SHORT).show();
        EditText et = findViewById(R.id.etSearch);
        if (et != null) { et.setText(name); et.setSelection(name.length()); }
        loadDoctors(selectedDepartment);
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        if (etSearch == null) return;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onResult(List<Doctor> data) {
                runOnUiThread(() -> {
                    doctors.clear();
                    if (data.isEmpty()) {
                        doctors.add(new Doctor("d1","Dr. Sarah Chen","Neurologist",department,"Mon-Fri 08:00-17:00"));
                        doctors.add(new Doctor("d2","Dr. Mugisha Eric","Cardiologist",department,"Mon-Fri 09:00-16:00"));
                        doctors.add(new Doctor("d3","Dr. Uwase Claire","Specialist",department,"Mon-Wed 09:00-15:00"));
                        doctors.add(new Doctor("d4","Dr. Habimana Jean","Dentist",department,"Tue-Thu 08:00-14:00"));
                    } else { doctors.addAll(data); }
                    if (!doctors.isEmpty()) { updateDoctorCard(doctors.get(0)); selectedDoctor = doctors.get(0); }
                    if (doctorAdapter != null) doctorAdapter.notifyDataSetChanged();
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    doctors.clear();
                    doctors.add(new Doctor("d1","Dr. Sarah Chen","Neurologist",department,"Mon-Fri 08:00-17:00"));
                    doctors.add(new Doctor("d2","Dr. Mugisha Eric","Cardiologist",department,"Mon-Fri 09:00-16:00"));
                    if (!doctors.isEmpty()) { updateDoctorCard(doctors.get(0)); selectedDoctor = doctors.get(0); }
                    if (doctorAdapter != null) doctorAdapter.notifyDataSetChanged();
                });
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
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this,
                        message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void bookAppointment() {
        if (selectedDoctor == null) {
            Toast.makeText(this, getString(R.string.select_provider), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDateTimeSelected) {
            Toast.makeText(this, getString(R.string.select_session), Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = session.getUid();
        if (uid == null) return;

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm",      Locale.getDefault());

        Appointment appt = new Appointment(
                uid, selectedDoctor.getId(), selectedDoctor.getName(),
                selectedDepartment,
                dateSdf.format(selectedDateTime.getTime()),
                timeSdf.format(selectedDateTime.getTime()));

        appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
            @Override
            public void onResult(Appointment data) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this,
                            getString(R.string.book_appointment), Toast.LENGTH_SHORT).show();
                    isDateTimeSelected = false;
                    TextView btnText = findViewById(R.id.btn_select_session);
                    if (btnText != null) btnText.setText(getString(R.string.select_session));
                    loadAppointments();
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this,
                        message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        selectedDoctor = doctor;
        updateDoctorCard(doctor);
        if (rvDoctors != null) rvDoctors.setVisibility(View.GONE);
        Toast.makeText(this, doctor.getName(), Toast.LENGTH_SHORT).show();
    }

    private void setupTimeSlots() {
        for (int i = 0; i < slotIds.length; i++) {
            TextView slot = findViewById(slotIds[i]);
            if (slot == null) continue;
            if (!slotAvailable[i]) { slot.setAlpha(0.5f); continue; }
            final String time = slotTimes[i];
            slot.setOnClickListener(v -> selectTimeSlot(time));
        }
        selectTimeSlot("10:30");
    }

    private void selectTimeSlot(String time) {
        selectedTimeSlot = time;
        for (int i = 0; i < slotIds.length; i++) {
            TextView slot = findViewById(slotIds[i]);
            if (slot == null || !slotAvailable[i]) continue;
            if (slotTimes[i].equals(time)) { slot.setBackgroundResource(R.drawable.bg_slot_selected); slot.setTextColor(0xFFFFFFFF); }
            else { slot.setBackgroundResource(R.drawable.bg_slot_available); slot.setTextColor(0xFF3D7FE8); }
        }
    }

    private void setupDayPicker() {
        TextView slot = findViewById(R.id.tvSlot0900);
        if (slot == null) return;
        if (slot.getParent() instanceof LinearLayout) {
            View slotRow = (View) slot.getParent();
            if (slotRow.getParent() instanceof LinearLayout)
                ((LinearLayout) slotRow.getParent()).setOnClickListener(v -> openDatePicker());
        }
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
        View btnBook = findViewById(R.id.btnBookAppointment);
        if (btnBook != null) btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment() {
        if (selectedDoctor == null)  { Toast.makeText(this, "Please select a doctor first", Toast.LENGTH_SHORT).show(); return; }
        if (selectedTimeSlot == null){ Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show(); return; }
        String uid = session.getUid();
        if (uid == null) { Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return; }

        Appointment appt = new Appointment(uid, selectedDoctor.getId(), selectedDoctor.getName(), selectedDepartment, selectedDate, selectedTimeSlot);
        View bookBtn = findViewById(R.id.btnBookAppointment);
        if (bookBtn != null) bookBtn.setEnabled(false);

        appointmentRepo.book(appt, new AppointmentRepository.Callback<Appointment>() {
            @Override public void onResult(Appointment data) {
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

    @Override
    public void onReschedule(Appointment appointment) {
        appointmentRepo.reschedule(appointment.getId(), selectedDate, selectedTimeSlot,
                new AppointmentRepository.Callback<Void>() {
                    @Override public void onResult(Void data) {
                        runOnUiThread(() -> {
                            Toast.makeText(AppointmentManagementActivity.this,
                                    getString(R.string.reschedule) + ": " + newDate + " " + newTime,
                                    Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AppointmentManagementActivity.this,
                                    getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        });
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
