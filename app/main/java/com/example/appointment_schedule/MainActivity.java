package com.example.appointment_schedule;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Doctor selectedDoctor = null;
    private String selectedDate = "";
    private String selectedTime = "";
    private String selectedDepartment = "Neurology";
    private String currentCareHubTab = "Upcoming";

    private TextView tvCardiology, tvNeurology, tvDentistry, txtCurrentDate;
    private TextView tabUpcoming, tabRescheduled, tabCancelled;
    private List<Doctor> allDoctors = new ArrayList<>();
    private List<Appointment> myAppointments = new ArrayList<>();
    private RecyclerView rvDoctors, rvMyAppointments;
    private DoctorAdapter doctorAdapter;
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        setupDoctorData();
        setupDepartmentSelectors();
        setupScheduleList();
        updateDateHeader();
        setupCareHubTabs();

        MaterialButton selectSessionBtn = findViewById(R.id.btn_select_session);
        if (selectSessionBtn != null) {
            selectSessionBtn.setOnClickListener(v -> showDatePicker(null));
        }

        MaterialButton bookBtn = findViewById(R.id.btn_book_appointment);
        if (bookBtn != null) {
            bookBtn.setOnClickListener(v -> attemptBooking());
        }
        
        filterDoctors("Neurology");
    }

    private void initViews() {
        tvCardiology = findViewById(R.id.dept_cardiology);
        tvNeurology = findViewById(R.id.dept_neurology);
        tvDentistry = findViewById(R.id.dept_dentistry);
        txtCurrentDate = findViewById(R.id.txt_current_date);
        rvDoctors = findViewById(R.id.rv_doctors);
        rvMyAppointments = findViewById(R.id.rv_my_appointments);

        tabUpcoming = findViewById(R.id.tab_upcoming);
        tabRescheduled = findViewById(R.id.tab_rescheduled);
        tabCancelled = findViewById(R.id.tab_cancelled);
        
        if (rvDoctors != null) rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        if (rvMyAppointments != null) rvMyAppointments.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupCareHubTabs() {
        View.OnClickListener listener = v -> {
            int id = v.getId();
            if (id == R.id.tab_upcoming) updateCareHubUI("Upcoming");
            else if (id == R.id.tab_rescheduled) updateCareHubUI("Rescheduled");
            else if (id == R.id.tab_cancelled) updateCareHubUI("Cancelled");
        };

        if (tabUpcoming != null) tabUpcoming.setOnClickListener(listener);
        if (tabRescheduled != null) tabRescheduled.setOnClickListener(listener);
        if (tabCancelled != null) tabCancelled.setOnClickListener(listener);

        updateCareHubUI("Upcoming");
    }

    private void updateCareHubUI(String tab) {
        currentCareHubTab = tab;
        if (tabUpcoming != null) resetTabStyle(tabUpcoming);
        if (tabRescheduled != null) resetTabStyle(tabRescheduled);
        if (tabCancelled != null) resetTabStyle(tabCancelled);

        if (tab.equals("Upcoming") && tabUpcoming != null) setActiveTabStyle(tabUpcoming);
        else if (tab.equals("Rescheduled") && tabRescheduled != null) setActiveTabStyle(tabRescheduled);
        else if (tab.equals("Cancelled") && tabCancelled != null) setActiveTabStyle(tabCancelled);

        refreshAppointmentsList();
    }

    private void refreshAppointmentsList() {
        List<Appointment> filtered = new ArrayList<>();
        String statusToMatch = "CONFIRMED";
        if (currentCareHubTab.equals("Rescheduled")) statusToMatch = "RESCHEDULED";
        else if (currentCareHubTab.equals("Cancelled")) statusToMatch = "CANCELLED";

        for (Appointment appt : myAppointments) {
            if (appt.getStatus().equals(statusToMatch)) {
                filtered.add(appt);
            }
        }

        appointmentAdapter = new AppointmentAdapter(filtered, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onReschedule(Appointment appointment) {
                showDatePicker(appointment);
            }

            @Override
            public void onCancel(Appointment appointment) {
                showCancelConfirmationDialog(appointment);
            }
        });
        if (rvMyAppointments != null) rvMyAppointments.setAdapter(appointmentAdapter);
    }

    private void resetTabStyle(TextView tv) {
        tv.setSelected(false);
        tv.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
    }

    private void setActiveTabStyle(TextView tv) {
        tv.setSelected(true);
        tv.setTextColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void updateDateHeader() {
        if (txtCurrentDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
            String date = sdf.format(new Date());
            txtCurrentDate.setText(date);
        }
    }

    private void setupDoctorData() {
        int placeholder = R.mipmap.ic_launcher;
        allDoctors.add(new Doctor("Dr. Sarah Chen", "Neurologist", "Available Today", placeholder));
        allDoctors.add(new Doctor("Dr. James Wilson", "Dentist", "Available Tomorrow", placeholder));
        allDoctors.add(new Doctor("Dr. Emily Blunt", "Dentist", "Available Today", placeholder));
        allDoctors.add(new Doctor("Dr. Michael Page", "Cardiologist", "Available in 2 days", placeholder));
        allDoctors.add(new Doctor("Dr. Alan Grant", "Neurologist", "Available Friday", placeholder));
        allDoctors.add(new Doctor("Dr. Ellie Sattler", "Dentist", "Available Today", placeholder));
    }

    private void setupDepartmentSelectors() {
        View.OnClickListener listener = v -> {
            int id = v.getId();
            if (id == R.id.dept_cardiology) updateDepartmentUI("Cardiology");
            else if (id == R.id.dept_neurology) updateDepartmentUI("Neurology");
            else if (id == R.id.dept_dentistry) updateDepartmentUI("Dentistry");
        };

        if (tvCardiology != null) tvCardiology.setOnClickListener(listener);
        if (tvNeurology != null) tvNeurology.setOnClickListener(listener);
        if (tvDentistry != null) tvDentistry.setOnClickListener(listener);
        
        updateDepartmentUI("Neurology");
    }

    private void updateDepartmentUI(String dept) {
        selectedDepartment = dept;
        if (tvCardiology != null) resetDeptStyle(tvCardiology);
        if (tvNeurology != null) resetDeptStyle(tvNeurology);
        if (tvDentistry != null) resetDeptStyle(tvDentistry);

        if (dept.equals("Cardiology") && tvCardiology != null) setActiveDeptStyle(tvCardiology);
        else if (dept.equals("Neurology") && tvNeurology != null) setActiveDeptStyle(tvNeurology);
        else if (dept.equals("Dentistry") && tvDentistry != null) setActiveDeptStyle(tvDentistry);

        filterDoctors(dept);
    }

    private void resetDeptStyle(TextView tv) {
        tv.setBackgroundResource(R.drawable.selector_item_bg);
        tv.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
    }

    private void setActiveDeptStyle(TextView tv) {
        tv.setBackgroundResource(R.drawable.selected_item_bg);
        tv.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void filterDoctors(String specialty) {
        List<Doctor> filtered = new ArrayList<>();
        String matchTerm = specialty.equals("Dentistry") ? "Dentist" : 
                          specialty.equals("Cardiology") ? "Cardiologist" : "Neurologist";
        
        for (Doctor d : allDoctors) {
            if (d.getSpecialty().equals(matchTerm)) {
                filtered.add(d);
            }
        }
        
        doctorAdapter = new DoctorAdapter(filtered, doctor -> {
            selectedDoctor = doctor;
        });
        if (rvDoctors != null) rvDoctors.setAdapter(doctorAdapter);
        selectedDoctor = null;
    }

    private void setupScheduleList() {
        refreshAppointmentsList();
    }

    private void showCancelConfirmationDialog(Appointment appointment) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel your appointment with " + appointment.getDoctor().getName() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    appointment.setStatus("CANCELLED");
                    refreshAppointmentsList();
                    Toast.makeText(MainActivity.this, "Appointment Cancelled", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No, Keep it", null)
                .show();
    }

    private void showDatePicker(Appointment appointmentToReschedule) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String newDate = sdf.format(new Date(selection));
            showTimePicker(appointmentToReschedule, newDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker(Appointment appointmentToReschedule, String newDate) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(10)
                .setMinute(30)
                .setTitleText("Select Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            String amPm = timePicker.getHour() >= 12 ? "PM" : "AM";
            int hour = timePicker.getHour() % 12;
            if (hour == 0) hour = 12;
            String newTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, timePicker.getMinute(), amPm);
            
            if (appointmentToReschedule != null) {
                appointmentToReschedule.setDate(newDate);
                appointmentToReschedule.setTime(newTime);
                appointmentToReschedule.setStatus("RESCHEDULED");
                refreshAppointmentsList();
                Toast.makeText(this, "Rescheduled Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                selectedDate = newDate;
                selectedTime = newTime;
                MaterialButton btn = findViewById(R.id.btn_select_session);
                if (btn != null) btn.setText(selectedDate + " at " + selectedTime);
            }
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void attemptBooking() {
        if (selectedDoctor == null) {
            Toast.makeText(this, "Please select a specialist", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment newAppointment = new Appointment(
                String.valueOf(System.currentTimeMillis()),
                selectedDoctor,
                selectedDate,
                selectedTime,
                "CONFIRMED"
        );
        myAppointments.add(newAppointment);
        refreshAppointmentsList();
        
        Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_LONG).show();
        
        // Reset for next booking
        selectedDate = "";
        selectedTime = "";
        MaterialButton btn = findViewById(R.id.btn_select_session);
        if (btn != null) btn.setText("Select Session");
    }
}
