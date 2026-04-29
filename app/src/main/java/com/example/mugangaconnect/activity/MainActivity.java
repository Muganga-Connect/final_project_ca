package com.example.mugangaconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.NoShowPredictor;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private AppointmentRepository appointmentRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);

        bindUserName();
        loadDashboardData();
        wireButtons();

        BottomNavHelper.setup(this, BottomNavHelper.Screen.DASHBOARD);
    }

    private void bindUserName() {
        String name = session.getFullName();
        if (name != null && !name.isEmpty()) {
            TextView tvName = findTextViewContaining("Alexandrine");
            if (tvName != null) tvName.setText(name);
        }
    }

    /** Find the first TextView whose current text contains the given substring. */
    private TextView findTextViewContaining(String text) {
        android.view.View root = getWindow().getDecorView();
        return findIn(root, text);
    }

    private TextView findIn(android.view.View v, String text) {
        if (v instanceof TextView) {
            if (((TextView) v).getText().toString().contains(text)) return (TextView) v;
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                TextView found = findIn(vg.getChildAt(i), text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void loadDashboardData() {
        String uid = session.getUid();
        if (uid == null) return;

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onResult(List<Appointment> data) {
                // Find next upcoming appointment
                Appointment next = null;
                for (Appointment a : data) {
                    if (Appointment.Status.UPCOMING.name().equals(a.getStatus())) {
                        if (next == null || a.getDate().compareTo(next.getDate()) < 0) next = a;
                    }
                }
                final Appointment upcoming = next;

                // Compute risk from stats
                appointmentRepo.getMissedStats(uid, new AppointmentRepository.Callback<int[]>() {
                    @Override
                    public void onResult(int[] stats) {
                        String risk = NoShowPredictor.predict(stats[0], stats[1]);
                        runOnUiThread(() -> updateDashboardUI(upcoming, risk, stats[0], stats[1]));
                    }
                    @Override public void onError(String message) {}
                });
            }
            @Override public void onError(String message) {}
        });
    }

    private void updateDashboardUI(Appointment upcoming, String risk, int missed, int total) {
        // Update upcoming appointment card
        TextView tvDoctor = findTextViewContaining("Dr. Diane");
        if (tvDoctor != null && upcoming != null) tvDoctor.setText(upcoming.getDoctorName());

        TextView tvDateTime = findTextViewContaining("April 24");
        if (tvDateTime != null && upcoming != null)
            tvDateTime.setText(upcoming.getDate() + "\n" + upcoming.getTime());

        // Update smart alert
        TextView tvAlert = findTextViewContaining("2 of");
        if (tvAlert != null) {
            if (total >= 3 && missed > 0) {
                tvAlert.setText(missed + " of last " + total + " appointments were missed.");
            } else {
                tvAlert.setText("You're on track! Keep attending your appointments.");
            }
        }
    }

    private void wireButtons() {
        // "View Appointment History" card
        TextView tvHistory = findTextViewContaining("View Appointment History");
        if (tvHistory != null) {
            tvHistory.setOnClickListener(v ->
                    startActivity(new Intent(this, AppointmentHistoryActivity.class)));
        }
    }
}
