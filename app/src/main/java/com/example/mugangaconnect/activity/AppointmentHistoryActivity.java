package com.example.mugangaconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

    private AppointmentRepository appointmentRepo;
    private SessionManager        session;
    private AppointmentAdapter    adapter;

    private final List<Appointment> allAppointments = new ArrayList<>();
    private final List<Appointment> appointments    = new ArrayList<>();

    private String activeStatus = Appointment.Status.ATTENDED.name();
    private int    currentPage  = 0;
    private static final int PAGE_SIZE = 10;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_history);

        session         = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);

        setupRecyclerView();
        setupTabs();
        setupSearch();
        setupMenu();
        setupBackButton();
        setupMoreButton();

        syncAndLoad();
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_history);
        if (rv == null) return;
        adapter = new AppointmentAdapter(appointments, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void setupTabs() {
        int[]    tabIds   = {R.id.tabCompleted, R.id.tabCancelled, R.id.tabMissed};
        int[]    textIds  = {R.id.textCompleted, R.id.textCancelled, R.id.textMissed};
        String[] statuses = {
            Appointment.Status.ATTENDED.name(),
            Appointment.Status.CANCELLED.name(),
            Appointment.Status.MISSED.name()
        };
        for (int i = 0; i < tabIds.length; i++) {
            MaterialCardView tab = findViewById(tabIds[i]);
            if (tab == null) continue;
            final String status = statuses[i];
            final int    idx    = i;
            tab.setOnClickListener(v -> {
                activeStatus = status;
                currentPage  = 0;
                highlightTab(tabIds, textIds, idx);
                filterAndDisplay();
            });
        }
        highlightTab(tabIds, textIds, 0);
    }

    private void highlightTab(int[] tabIds, int[] textIds, int activeIdx) {
        for (int i = 0; i < tabIds.length; i++) {
            MaterialCardView tab = findViewById(tabIds[i]);
            TextView         tv  = findViewById(textIds[i]);
            if (tab == null || tv == null) continue;
            if (i == activeIdx) {
                tab.setCardBackgroundColor(0xFF1A4C91);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tab.setCardBackgroundColor(0xFFFFFFFF);
                tv.setTextColor(0xFF6C96C3);
                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etHistorySearch);
        if (etSearch == null) return;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPage = 0;
                filterAndDisplay();
            }
        });
    }

    private void setupMenu() {
        MaterialCardView menuCard = findViewById(R.id.menuCard);
        if (menuCard == null) return;
        menuCard.setOnClickListener(v -> {
            String[] options = {"Dashboard", "Book Appointment", "AI Assistant", "Profile"};
            new AlertDialog.Builder(this)
                    .setTitle("Navigate to")
                    .setItems(options, (dialog, which) -> {
                        switch (which) {
                            case 0: startActivity(new Intent(this, MainActivity.class)); break;
                            case 1: startActivity(new Intent(this, AppointmentManagementActivity.class)); break;
                            case 2: startActivity(new Intent(this, AIAssistantActivity.class)); break;
                            case 3: startActivity(new Intent(this, ProfileActivity.class)); break;
                        }
                    }).show();
        });
    }

    private void setupBackButton() {
        ImageView backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());
    }

    private void setupMoreButton() {
        MaterialCardView moreBtn = findViewById(R.id.moreBtn);
        if (moreBtn == null) return;
        moreBtn.setOnClickListener(v -> {
            EditText etSearch = findViewById(R.id.etHistorySearch);
            String query = etSearch != null ? etSearch.getText().toString().trim().toLowerCase() : "";
            List<Appointment> filtered = applyFilters(allAppointments, activeStatus, query);
            int start = (currentPage + 1) * PAGE_SIZE;
            if (start >= filtered.size()) {
                Toast.makeText(this, "No more appointments", Toast.LENGTH_SHORT).show();
                return;
            }
            currentPage++;
            int end = Math.min(start + PAGE_SIZE, filtered.size());
            appointments.addAll(filtered.subList(start, end));
            if (adapter != null) adapter.notifyDataSetChanged();
        });
    }

    private void syncAndLoad() {
        String uid = session.getUid();
        if (uid == null) return;
        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onResult(List<Appointment> data) {
                runOnUiThread(() -> {
                    allAppointments.clear();
                    allAppointments.addAll(data);
                    currentPage = 0;
                    filterAndDisplay();
                });
            }
            @Override
            public void onError(String message) {
                appointmentRepo.getCachedByStatus(session.getUid(), activeStatus,
                        new AppointmentRepository.Callback<List<Appointment>>() {
                            @Override public void onResult(List<Appointment> data) {
                                runOnUiThread(() -> {
                                    allAppointments.clear();
                                    allAppointments.addAll(data);
                                    currentPage = 0;
                                    filterAndDisplay();
                                });
                            }
                            @Override public void onError(String msg) {
                                runOnUiThread(() -> Toast.makeText(AppointmentHistoryActivity.this, msg, Toast.LENGTH_SHORT).show());
                            }
                        });
            }
        });
    }

    private void filterAndDisplay() {
        EditText etSearch = findViewById(R.id.etHistorySearch);
        String query = etSearch != null ? etSearch.getText().toString().trim().toLowerCase() : "";
        List<Appointment> filtered = applyFilters(allAppointments, activeStatus, query);
        int end = Math.min(PAGE_SIZE, filtered.size());
        appointments.clear();
        if (!filtered.isEmpty()) appointments.addAll(filtered.subList(0, end));
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private List<Appointment> applyFilters(List<Appointment> source, String status, String query) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : source) {
            if (!status.equals(a.getStatus())) continue;
            if (!query.isEmpty()
                    && !a.getDoctorName().toLowerCase().contains(query)
                    && !a.getDepartment().toLowerCase().contains(query)
                    && !a.getDate().toLowerCase().contains(query)) continue;
            out.add(a);
        }
        return out;
    }

    @Override public void onReschedule(Appointment appointment) {
        startActivity(new Intent(this, AppointmentManagementActivity.class));
    }
    @Override public void onCancel(Appointment appointment) {
        Toast.makeText(this, "Cannot cancel a past appointment", Toast.LENGTH_SHORT).show();
    }
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
                    syncAndLoad();
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(AppointmentHistoryActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
