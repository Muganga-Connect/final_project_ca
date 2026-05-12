package com.example.mugangaconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.ui.adapter.AppointmentAdapter;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity 
        implements AppointmentAdapter.OnAppointmentActionListener {

    private AppointmentRepository appointmentRepo;
    private SessionManager        session;
    
    private RecyclerView       rvAppointments;
    private AppointmentAdapter appointmentAdapter;
    private final List<Appointment> appointmentList = new ArrayList<>();

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

        setupTabs();
        setupRecyclerView();
        setupBookButton();

        BottomNavHelper.setup(this, BottomNavHelper.Screen.SCHEDULE);
        setupViewAll();
        loadAppointments("UPCOMING");
    }

    private void setupViewAll() {
        View tvViewAll = findViewById(R.id.tvViewAll);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                loadAppointments("ALL");
            });
        }
    }

    private void setupTabs() {
        View tabUpcoming    = findViewById(R.id.tabUpcoming);
        View tabRescheduled = findViewById(R.id.tabRescheduled);
        View tabCancelled   = findViewById(R.id.tabCancelledHub);

        if (tabUpcoming != null) tabUpcoming.setOnClickListener(v -> loadAppointments("UPCOMING"));
        if (tabRescheduled != null) tabRescheduled.setOnClickListener(v -> loadAppointments("RESCHEDULED"));
        if (tabCancelled != null) tabCancelled.setOnClickListener(v -> loadAppointments("CANCELLED"));
    }

    private void setupRecyclerView() {
        rvAppointments = findViewById(R.id.rv_appointments);
        if (rvAppointments != null) {
            appointmentAdapter = new AppointmentAdapter(appointmentList, this);
            rvAppointments.setLayoutManager(new LinearLayoutManager(this));
            rvAppointments.setAdapter(appointmentAdapter);
        }
    }

    private void setupBookButton() {
        View btnBook = findViewById(R.id.btn_book_appointment);
        if (btnBook != null) {
            btnBook.setOnClickListener(v -> {
                startActivity(new Intent(this, AppointmentBookingActivity.class));
            });
        }
    }

    private void loadAppointments(String status) {
        String uid = session.getUid();
        if (uid == null) return;

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                runOnUiThread(() -> {
                    appointmentList.clear();
                    // Filter by status if needed, or just show all for now
                    for (Appointment appt : result) {
                        if (status.equals("ALL") || appt.getStatus().equalsIgnoreCase(status)) {
                            appointmentList.add(appt);
                        }
                    }
                    if (appointmentAdapter != null) appointmentAdapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onReschedule(Appointment appointment) {
        Intent intent = new Intent(this, AppointmentBookingActivity.class);
        intent.putExtra("reschedule_id", appointment.getId());
        intent.putExtra("doctor_name", appointment.getDoctorName());
        intent.putExtra("department", appointment.getDepartment());
        startActivity(intent);
    }

    @Override
    public void onCancel(Appointment appointment) {
        appointmentRepo.updateStatus(appointment.getId(), session.getUid(), "CANCELLED", new AppointmentRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    loadAppointments("UPCOMING");
                });
            }
            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onMarkAttended(Appointment appointment) {}

    @Override
    public void onMarkMissed(Appointment appointment) {}
}
