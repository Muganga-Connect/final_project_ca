package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.ui.adapter.AppointmentAdapter;
import com.example.mugangaconnect.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentHistoryActivity extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener {

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
        appointmentRepo = new AppointmentRepository(this);

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
            public void onResult(List<Appointment> data) {
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
        int[] textIds  = {R.id.textCompleted, R.id.textCancelled, R.id.textMissed};
        String[] statuses = {
            Appointment.Status.ATTENDED.name(),
            Appointment.Status.CANCELLED.name(),
            Appointment.Status.MISSED.name()
        };
        for (int i = 0; i < tabIds.length; i++) {
            MaterialCardView tab = findViewById(tabIds[i]);
            if (tab == null) continue;
            int index = i;
            tab.setOnClickListener(v -> {
                activeStatus = statuses[index];
                updateTabUI(tabIds, textIds, index);
                loadByStatus(activeStatus);
            });
        }
        updateTabUI(tabIds, textIds, 0); // Default to first tab
    }

    private void updateTabUI(int[] tabIds, int[] textIds, int activeIndex) {
        for (int i = 0; i < tabIds.length; i++) {
            MaterialCardView tab = findViewById(tabIds[i]);
            TextView text = findViewById(textIds[i]);
            if (tab == null || text == null) continue;

            if (i == activeIndex) {
                tab.setCardBackgroundColor(android.graphics.Color.parseColor("#1A4C91"));
                text.setTextColor(android.graphics.Color.WHITE);
                text.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tab.setCardBackgroundColor(android.graphics.Color.WHITE);
                text.setTextColor(android.graphics.Color.parseColor("#6C96C3"));
                text.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void loadByStatus(String status) {
        String uid = session.getUid();
        if (uid == null) return;
        appointmentRepo.getCachedByStatus(uid, status,
                new AppointmentRepository.Callback<List<Appointment>>() {
                    @Override public void onResult(List<Appointment> data) {
                        runOnUiThread(() -> {
                            appointments.clear();
                            appointments.addAll(data);
                            if (adapter != null) adapter.notifyDataSetChanged();
                        });
                    }
                    @Override public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(AppointmentHistoryActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    @Override public void onReschedule(Appointment appointment) { /* history is read-only */ }
    @Override public void onCancel(Appointment appointment)     { /* history is read-only */ }
    @Override public void onMarkAttended(Appointment appointment) {
        updateStatus(appointment, Appointment.Status.ATTENDED.name());
    }
    @Override public void onMarkMissed(Appointment appointment) {
        updateStatus(appointment, Appointment.Status.MISSED.name());
    }

    private void updateStatus(Appointment appointment, String status) {
        String uid = session.getUid();
        appointmentRepo.updateStatus(appointment.getId(), uid, status, new AppointmentRepository.Callback<Void>() {
            @Override
            public void onResult(Void data) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentHistoryActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                    loadByStatus(activeStatus);
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentHistoryActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
