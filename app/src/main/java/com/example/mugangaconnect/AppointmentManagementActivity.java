package com.example.mugangaconnect;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AppointmentManagementActivity extends AppCompatActivity {

    private TextView deptCardiology, deptNeurology;
    private TextView tabUpcoming, tabRescheduled, tabCancelledHub;
    private MaterialCardView appointmentCard1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_management);

        // Initialize Department Selection
        deptCardiology = findViewById(R.id.deptCardiology);
        deptNeurology = findViewById(R.id.deptNeurology);

        deptCardiology.setOnClickListener(v -> selectDepartment("cardiology"));
        deptNeurology.setOnClickListener(v -> selectDepartment("neurology"));

        // Initialize Hub Tabs
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabRescheduled = findViewById(R.id.tabRescheduled);
        tabCancelledHub = findViewById(R.id.tabCancelledHub);
        appointmentCard1 = findViewById(R.id.appointmentCard1);

        tabUpcoming.setOnClickListener(v -> selectHubTab("upcoming"));
        tabRescheduled.setOnClickListener(v -> selectHubTab("rescheduled"));
        tabCancelledHub.setOnClickListener(v -> selectHubTab("cancelled"));

        // Buttons
        findViewById(R.id.btnSelectSession).setOnClickListener(v -> 
            Toast.makeText(this, "Opening session selection...", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnConfirmPresence).setOnClickListener(v -> 
            Toast.makeText(this, "Presence Confirmed!", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnReschedule).setOnClickListener(v -> 
            Toast.makeText(this, "Opening reschedule calendar...", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnCancel).setOnClickListener(v -> 
            Toast.makeText(this, "Appointment Cancelled", Toast.LENGTH_SHORT).show());

        // Recent Hospitals
        findViewById(R.id.hospitalCityGeneral).setOnClickListener(v -> searchHospital("City General"));
        findViewById(R.id.hospitalMayo).setOnClickListener(v -> searchHospital("Mayo Clinic"));
        findViewById(R.id.hospitalStMarys).setOnClickListener(v -> searchHospital("St. Mary's"));

        // Navigation
        BottomNavHelper.setup(this, BottomNavHelper.Screen.SCHEDULE);
    }

    private void selectDepartment(String dept) {
        if (dept.equals("cardiology")) {
            deptCardiology.setBackgroundColor(Color.parseColor("#1E88E5"));
            deptCardiology.setTextColor(Color.WHITE);
            deptNeurology.setBackgroundColor(Color.parseColor("#F5F9FD"));
            deptNeurology.setTextColor(Color.parseColor("#1A4C91"));
        } else {
            deptNeurology.setBackgroundColor(Color.parseColor("#1E88E5"));
            deptNeurology.setTextColor(Color.WHITE);
            deptCardiology.setBackgroundColor(Color.parseColor("#F5F9FD"));
            deptCardiology.setTextColor(Color.parseColor("#1A4C91"));
        }
        Toast.makeText(this, "Selected: " + dept, Toast.LENGTH_SHORT).show();
    }

    private void selectHubTab(String status) {
        // Reset styles
        tabUpcoming.setTextColor(Color.parseColor("#8AA6C7"));
        tabUpcoming.setBackgroundColor(Color.TRANSPARENT);
        tabRescheduled.setTextColor(Color.parseColor("#8AA6C7"));
        tabRescheduled.setBackgroundColor(Color.TRANSPARENT);
        tabCancelledHub.setTextColor(Color.parseColor("#8AA6C7"));
        tabCancelledHub.setBackgroundColor(Color.TRANSPARENT);

        // Highlight selected
        TextView selected = null;
        if (status.equals("upcoming")) selected = tabUpcoming;
        else if (status.equals("rescheduled")) selected = tabRescheduled;
        else if (status.equals("cancelled")) selected = tabCancelledHub;

        if (selected != null) {
            selected.setTextColor(Color.parseColor("#1E88E5"));
            selected.setBackgroundColor(Color.WHITE);
        }

        // Simple visibility logic
        if (status.equals("upcoming")) {
            appointmentCard1.setVisibility(View.VISIBLE);
        } else {
            appointmentCard1.setVisibility(View.GONE);
            Toast.makeText(this, "No " + status + " appointments found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchHospital(String name) {
        Toast.makeText(this, "Searching for: " + name, Toast.LENGTH_SHORT).show();
    }
}
