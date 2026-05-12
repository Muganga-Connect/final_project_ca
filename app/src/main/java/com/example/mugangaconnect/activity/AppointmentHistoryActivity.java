package com.example.mugangaconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.ui.adapter.AppointmentAdapter;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentHistoryActivity extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    private AppointmentRepository appointmentRepo;
    private SessionManager session;
    private AppointmentAdapter adapter;
    private final List<Appointment> appointments = new ArrayList<>();
    private String activeStatus = Appointment.Status.ATTENDED.name();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_history);

        session         = new SessionManager(this);
        appointmentRepo = new AppointmentRepository();

        RecyclerView rv = findViewById(R.id.rv_history);
        if (rv != null) {
            adapter = new AppointmentAdapter(appointments, this);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(adapter);
        }

        setupTabs();

        ImageView backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        MaterialCardView moreBtn = findViewById(R.id.moreBtn);
        if (moreBtn != null) moreBtn.setOnClickListener(v ->
                Toast.makeText(this, "No more appointments", Toast.LENGTH_SHORT).show());

        syncAndLoad();
    }

    private void syncAndLoad() {
        String uid = session.getUid();
        if (uid == null) return;

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> data) {
                loadByStatus(activeStatus);
            }

            @Override
            public void onError(String message) {
                loadByStatus(activeStatus);
            }
        });
    }

    private void setupTabs() {
        int[] tabIds   = {R.id.tabCompleted, R.id.tabCancelled, R.id.tabMissed};
        String[] statuses = {
            Appointment.Status.ATTENDED.name(),
            Appointment.Status.CANCELLED.name(),
            Appointment.Status.MISSED.name()
        };
        for (int i = 0; i < tabIds.length; i++) {
            MaterialCardView tab = findViewById(tabIds[i]);
            if (tab == null) continue;
            String status = statuses[i];
            tab.setOnClickListener(v -> {
                activeStatus = status;
                loadByStatus(status);
            });
        }
    }

    private void loadByStatus(String status) {
        String uid = session.getUid();
        if (uid == null) return;
        appointmentRepo.getCachedByStatus(uid, status,
                new AppointmentRepository.Callback<List<Appointment>>() {
                    @Override public void onSuccess(List<Appointment> data) {
                        runOnUiThread(() -> {
                            appointments.clear();
                            if (status.equalsIgnoreCase(Appointment.Status.CANCELLED.name())) {
                                // Filter: canceled for 4 days
                                long fourDaysAgo = System.currentTimeMillis() - (4L * 24 * 60 * 60 * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                for (Appointment appt : data) {
                                    try {
                                        java.util.Date apptDate = sdf.parse(appt.getDate());
                                        if (apptDate != null && apptDate.getTime() >= fourDaysAgo) {
                                            appointments.add(appt);
                                        }
                                    } catch (Exception e) {
                                        // If date parsing fails, include it just in case or skip
                                        appointments.add(appt);
                                    }
                                }
                            } else {
                                appointments.addAll(data);
                            }
                            if (adapter != null) adapter.notifyDataSetChanged();
                        });
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentHistoryActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
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
                    Toast.makeText(AppointmentHistoryActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    loadByStatus(activeStatus);
                });
            }
            @Override
            public void onError(String errorMessage) {}
        });
    }
}
