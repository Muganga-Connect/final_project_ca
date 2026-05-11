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
        appointmentRepo = new AppointmentRepository(this);
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                if (query.isEmpty()) { loadDoctors(selectedDepartment); return; }
                List<Doctor> filtered = new ArrayList<>();
                for (Doctor d : doctors) {
                    if (d.getName().toLowerCase().contains(query) || d.getSpecialty().toLowerCase().contains(query) || d.getDepartment().toLowerCase().contains(query))
                        filtered.add(d);
                }
                if (!filtered.isEmpty()) { updateDoctorCard(filtered.get(0)); selectedDoctor = filtered.get(0); }
                if (doctorAdapter != null) { doctors.clear(); doctors.addAll(filtered); doctorAdapter.notifyDataSetChanged(); }
            }
        });
    }

    private void setupDepartmentSelection() {
        LinearLayout llCardiology = findViewById(R.id.llCardiology);
        LinearLayout llNeurology  = findViewById(R.id.llNeurology);
        LinearLayout llDentistry  = findViewById(R.id.llDentistry);
        if (llCardiology != null) llCardiology.setOnClickListener(v -> selectDepartment("Cardiology", llCardiology));
        if (llNeurology  != null) llNeurology .setOnClickListener(v -> selectDepartment("Neurology",  llNeurology));
        if (llDentistry  != null) llDentistry .setOnClickListener(v -> selectDepartment("Dentistry",  llDentistry));
        if (llCardiology != null) selectDepartment("Cardiology", llCardiology);
    }

    private void selectDepartment(String dept, View selectedView) {
        selectedDepartment = dept;
        int[] ids = {R.id.llCardiology, R.id.llNeurology, R.id.llDentistry};
        for (int id : ids) {
            View v = findViewById(id);
            if (v != null) v.setBackgroundResource(R.drawable.bg_department_item_normal);
            if (v instanceof LinearLayout) for (int i = 0; i < ((LinearLayout)v).getChildCount(); i++) { View c = ((LinearLayout)v).getChildAt(i); if (c instanceof TextView) ((TextView)c).setTextColor(0xFF1A2340); }
        }
        selectedView.setBackgroundResource(R.drawable.bg_department_item_selected);
        if (selectedView instanceof LinearLayout) for (int i = 0; i < ((LinearLayout)selectedView).getChildCount(); i++) { View c = ((LinearLayout)selectedView).getChildAt(i); if (c instanceof TextView) ((TextView)c).setTextColor(0xFFFFFFFF); }
        loadDoctors(dept);
    }

    private void loadDoctors(String department) {
        doctorRepo.getByDepartment(department, new DoctorRepository.Callback<List<Doctor>>() {
            @Override public void onResult(List<Doctor> data) {
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

    private void updateDoctorCard(Doctor doctor) {
        TextView tvName = findViewById(R.id.tvDoctorName);
        if (tvName != null) tvName.setText(doctor.getName());
        if (tvName != null && tvName.getParent() instanceof LinearLayout) {
            LinearLayout parent = (LinearLayout) tvName.getParent();
            if (parent.getChildCount() > 1 && parent.getChildAt(1) instanceof TextView)
                ((TextView) parent.getChildAt(1)).setText(doctor.getSpecialty());
        }
    }

    private void setupChooseAnotherDoctor() {
        LinearLayout llChoose = findViewById(R.id.llChooseAnotherDoctor);
        if (llChoose == null) return;
        llChoose.setOnClickListener(v -> {
            if (rvDoctors == null) {
                rvDoctors = new RecyclerView(this);
                rvDoctors.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                rvDoctors.setLayoutManager(new LinearLayoutManager(this));
                doctorAdapter = new DoctorAdapter(doctors, this);
                rvDoctors.setAdapter(doctorAdapter);
                if (llChoose.getParent() instanceof LinearLayout) {
                    LinearLayout parent = (LinearLayout) llChoose.getParent();
                    parent.addView(rvDoctors, parent.indexOfChild(llChoose) + 1);
                }
            } else {
                rvDoctors.setVisibility(rvDoctors.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, "Rescheduled", Toast.LENGTH_SHORT).show());
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
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
