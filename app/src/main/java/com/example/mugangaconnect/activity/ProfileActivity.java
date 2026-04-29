package com.example.mugangaconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mugangaconnect.data.repository.AuthRepository;
import com.example.mugangaconnect.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private AuthRepository authRepo;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authRepo = new AuthRepository();
        session = new SessionManager(this);

        View root = findViewById(R.id.main);
        View scroll = findViewById(R.id.profileScroll);
        View bottomBar = findViewById(R.id.bottomBar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (scroll != null) scroll.setPadding(0, systemBars.top, 0, 0);
            if (bottomBar != null) bottomBar.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        setupAccountSettings();
        setupSupport();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.PROFILE);

        // Edit profile button
        findViewById(R.id.editProfileBtn).setOnClickListener(v ->
                Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show());

        // Language selector
        findViewById(R.id.languageSelector).setOnClickListener(v -> showLanguageDialog());

        // Biometric switch
        SwitchCompat biometricSwitch = findViewById(R.id.biometricSwitch);
        biometricSwitch.setOnCheckedChangeListener((btn, isChecked) ->
                Toast.makeText(this, "Biometrics " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show());

        // Log out
        findViewById(R.id.logoutBtn).setOnClickListener(v -> showLogoutDialog());
    }

    private void setupAccountSettings() {
        findViewById(R.id.personalInfoItem).setOnClickListener(v ->
                Toast.makeText(this, "Personal Information coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.securityPinItem).setOnClickListener(v ->
                startActivity(new Intent(this, SecurityPinActivity.class)));

        findViewById(R.id.notificationItem).setOnClickListener(v ->
                Toast.makeText(this, "Notification Preferences coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.remindersItem).setOnClickListener(v ->
                Toast.makeText(this, "Manage Reminders coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupSupport() {
        findViewById(R.id.helpCenterItem).setOnClickListener(v ->
                Toast.makeText(this, "Help Center coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.privacyPolicyItem).setOnClickListener(v ->
                Toast.makeText(this, "Privacy Policy coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.termsItem).setOnClickListener(v ->
                Toast.makeText(this, "Terms of Service coming soon", Toast.LENGTH_SHORT).show());
    }

    private void showLanguageDialog() {
        String[] languages = {"English (EN)", "Français (FR)", "Kinyarwanda (RW)"};
        new AlertDialog.Builder(this)
                .setTitle("Select Language")
                .setItems(languages, (dialog, which) ->
                        Toast.makeText(this, languages[which] + " selected", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    authRepo.logout();
                    session.clearSession();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}