package com.example.mugangaconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.User;
import com.example.mugangaconnect.data.repository.AuthRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private AuthRepository authRepo;
    private SessionManager session;
    private TextView tvName, tvPatientId, tvWeight, tvBloodType, tvEmergency;
    private ImageView imgProfile;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authRepo = new AuthRepository();
        session  = new SessionManager(this);

        initViews();
        setupInsets();
        loadUserProfile();

        setupAccountSettings();
        setupSupport();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.PROFILE);

        findViewById(R.id.editProfileBtn).setOnClickListener(v ->
                startActivity(new Intent(this, PersonalInformationActivity.class)));

        updateLanguageButton();
        findViewById(R.id.languageSelector).setOnClickListener(v -> showLanguageDialog());

        SwitchCompat biometricSwitch = findViewById(R.id.biometricSwitch);
        biometricSwitch.setOnCheckedChangeListener((btn, isChecked) ->
                Toast.makeText(this,
                        getString(isChecked ? R.string.biometrics_enabled : R.string.biometrics_disabled),
                        Toast.LENGTH_SHORT).show());

        findViewById(R.id.logoutBtn).setOnClickListener(v -> showLogoutDialog());
    }

    private void initViews() {
        tvName = findViewById(R.id.profileName);
        tvPatientId = findViewById(R.id.patientId);
        tvWeight = findViewById(R.id.weightValue);
        tvBloodType = findViewById(R.id.bloodTypeValue);
        tvEmergency = findViewById(R.id.emergencyValue);
        imgProfile = findViewById(R.id.profileImage);
    }

    private void setupInsets() {
        View root = findViewById(R.id.main);
        View scroll = findViewById(R.id.profileScroll);
        View bottomBar = findViewById(R.id.bottomBar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (scroll    != null) scroll.setPadding(0, sys.top, 0, 0);
            if (bottomBar != null) bottomBar.setPadding(0, 0, 0, sys.bottom);
            return insets;
        });
    }

    private void loadUserProfile() {
        String uid = session.getUid();
        if (uid == null) return;

        // Load cached name first
        if (tvName != null) tvName.setText(session.getFullName());
        if (tvPatientId != null) tvPatientId.setText("PN-" + uid.substring(0, 5).toUpperCase());

        authRepo.getProfile(uid, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    if (tvName != null) tvName.setText(user.getFullName());
                    if (tvWeight != null) tvWeight.setText(user.getWeight() != null ? user.getWeight() + " kg" : "---");
                    if (tvBloodType != null) tvBloodType.setText(user.getBloodType() != null ? user.getBloodType() : "---");
                    if (tvEmergency != null) tvEmergency.setText(user.getEmergencyContact() != null ? user.getEmergencyContact() : "---");
                    
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.user)
                                .into(imgProfile);
                    }
                });
            }

            @Override
            public void onError(String message) {
                // Keep default/session values
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(); // Refresh data when coming back from edit screen
    }

    private void updateLanguageButton() {
        TextView btn = findViewById(R.id.languageSelector);
        if (btn == null) return;
        switch (session.getLanguage()) {
            case LocaleHelper.LANG_FRENCH:      btn.setText("FR ▼"); break;
            case LocaleHelper.LANG_KINYARWANDA: btn.setText("RW ▼"); break;
            default:                            btn.setText("EN ▼"); break;
        }
    }

    private void showLanguageDialog() {
        String[] labels = {"🇬🇧  English", "🇫🇷  Français", "🇷🇼  Kinyarwanda"};
        String[] codes  = {LocaleHelper.LANG_ENGLISH, LocaleHelper.LANG_FRENCH, LocaleHelper.LANG_KINYARWANDA};

        String current = session.getLanguage();
        int checkedItem = 0;
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(current)) { checkedItem = i; break; }
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(labels, checkedItem, (dialog, which) -> {
                    dialog.dismiss();
                    if (codes[which].equals(current)) return;

                    session.saveLanguage(codes[which]);
                    Intent intent = new Intent(this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void setupAccountSettings() {
        findViewById(R.id.personalInfoItem).setOnClickListener(v ->
                startActivity(new Intent(this, PersonalInformationActivity.class)));

        findViewById(R.id.securityPinItem).setOnClickListener(v ->
                startActivity(new Intent(this, SecurityPinActivity.class)));

        findViewById(R.id.notificationItem).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationPreferencesActivity.class)));

        findViewById(R.id.remindersItem).setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show());
    }

    private void setupSupport() {
        findViewById(R.id.helpCenterItem).setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show());

        findViewById(R.id.privacyPolicyItem).setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show());

        findViewById(R.id.termsItem).setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.logout_confirm), (dialog, which) -> {
                    authRepo.logout();
                    session.clearSession();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}