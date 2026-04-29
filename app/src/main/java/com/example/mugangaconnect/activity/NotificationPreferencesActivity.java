package com.example.mugangaconnect.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationPreferencesActivity extends AppCompatActivity {

    // UI elements
    private ImageButton btnBack;
    private SwitchMaterial switchEnableAll;
    private SwitchMaterial switchAppointment, switchSms, switchPush;
    private SwitchMaterial switchOneDay, switchTwoHours, switchThirtyMin;
    private MaterialButton btnSave;

    // SharedPreferences constants
    private static final String PREFS_NAME = "MugangaConnectPrefs";
    private static final String KEY_APPOINTMENT = "notification_appointmentReminders";
    private static final String KEY_SMS = "notification_smsNotifications";
    private static final String KEY_PUSH = "notification_pushNotifications";
    private static final String KEY_ONE_DAY = "reminder_oneDayBefore";
    private static final String KEY_TWO_HOURS = "reminder_twoHoursBefore";
    private static final String KEY_THIRTY_MIN = "reminder_thirtyMinBefore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preferences);

        // Initialize UI components
        initViews();

        // Load saved preferences
        loadPreferences();

        // Set up click listeners
        setupListeners();
    }

    /**
     * Initialize all views from the layout
     */
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        switchEnableAll = findViewById(R.id.switch_enable_all);
        
        // Reminders section
        switchAppointment = findViewById(R.id.switch_appointment);
        switchSms = findViewById(R.id.switch_sms);
        switchPush = findViewById(R.id.switch_push);
        
        // Timing section
        switchOneDay = findViewById(R.id.switch_one_day);
        switchTwoHours = findViewById(R.id.switch_two_hours);
        switchThirtyMin = findViewById(R.id.switch_thirty_min);
        
        btnSave = findViewById(R.id.btn_save_preferences);
    }

    /**
     * Load settings from SharedPreferences and apply to switches
     */
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        switchAppointment.setChecked(prefs.getBoolean(KEY_APPOINTMENT, true));
        switchSms.setChecked(prefs.getBoolean(KEY_SMS, false));
        switchPush.setChecked(prefs.getBoolean(KEY_PUSH, true));
        switchOneDay.setChecked(prefs.getBoolean(KEY_ONE_DAY, true));
        switchTwoHours.setChecked(prefs.getBoolean(KEY_TWO_HOURS, true));
        switchThirtyMin.setChecked(prefs.getBoolean(KEY_THIRTY_MIN, false));
        
        // Update "Enable All" state based on individual switches
        updateEnableAllState();
    }

    /**
     * Set up interaction listeners for all UI elements
     */
    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Enable All toggle logic
        switchEnableAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // Only trigger if manually toggled
                setAllSwitches(isChecked);
                String message = isChecked ? "All notifications enabled" : "All notifications disabled";
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Individual toggle listeners with Snackbars
        setupIndividualToggle(switchAppointment, "Appointment Reminders");
        setupIndividualToggle(switchSms, "SMS Notifications");
        setupIndividualToggle(switchPush, "Push Notifications");
        setupIndividualToggle(switchOneDay, "1 Day Before");
        setupIndividualToggle(switchTwoHours, "2 Hours Before");
        setupIndividualToggle(switchThirtyMin, "30 Minutes Before");

        // Save button logic
        btnSave.setOnClickListener(v -> savePreferences());
    }

    /**
     * Helper to set a listener for individual switches and show a Snackbar
     */
    private void setupIndividualToggle(SwitchMaterial toggle, String name) {
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                String state = isChecked ? "ON" : "OFF";
                Snackbar.make(findViewById(android.R.id.content), name + ": " + state, Snackbar.LENGTH_SHORT).show();
                updateEnableAllState(); // Check if Enable All should be updated
            }
        });
    }

    /**
     * Set the state of all notification switches
     */
    private void setAllSwitches(boolean state) {
        switchAppointment.setChecked(state);
        switchSms.setChecked(state);
        switchPush.setChecked(state);
        switchOneDay.setChecked(state);
        switchTwoHours.setChecked(state);
        switchThirtyMin.setChecked(state);
    }

    /**
     * Update Enable All switch based on if all other switches are ON
     */
    private void updateEnableAllState() {
        boolean allOn = switchAppointment.isChecked() && switchSms.isChecked() &&
                        switchPush.isChecked() && switchOneDay.isChecked() &&
                        switchTwoHours.isChecked() && switchThirtyMin.isChecked();
        
        // Remove listener temporarily to prevent loop
        switchEnableAll.setOnCheckedChangeListener(null);
        switchEnableAll.setChecked(allOn);
        // Re-attach listener
        switchEnableAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                setAllSwitches(isChecked);
                String message = isChecked ? "All notifications enabled" : "All notifications disabled";
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Persist settings to SharedPreferences
     */
    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        
        editor.putBoolean(KEY_APPOINTMENT, switchAppointment.isChecked());
        editor.putBoolean(KEY_SMS, switchSms.isChecked());
        editor.putBoolean(KEY_PUSH, switchPush.isChecked());
        editor.putBoolean(KEY_ONE_DAY, switchOneDay.isChecked());
        editor.putBoolean(KEY_TWO_HOURS, switchTwoHours.isChecked());
        editor.putBoolean(KEY_THIRTY_MIN, switchThirtyMin.isChecked());
        
        editor.apply();
        
        Toast.makeText(this, "Preferences saved successfully!", Toast.LENGTH_SHORT).show();
    }
}