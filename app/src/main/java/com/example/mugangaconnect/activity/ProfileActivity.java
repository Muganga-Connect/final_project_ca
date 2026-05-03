package com.example.mugangaconnect.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.mugangaconnect.data.repository.AuthRepository;
import com.example.mugangaconnect.utils.ImagePickerUtils;
import com.example.mugangaconnect.utils.ImageUploadUtils;
import com.example.mugangaconnect.utils.SessionManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity implements ImagePickerUtils.ImagePickerResultHandler {

    private AuthRepository authRepo;
    private SessionManager session;
    private ImageView profilePicture;
    private ImageUploadUtils imageUploadUtils;
    private ImagePickerUtils.ImagePickerCallback pendingImageCallback;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authRepo = new AuthRepository();
        session = new SessionManager(this);
        imageUploadUtils = new ImageUploadUtils(this);
        firestore = FirebaseFirestore.getInstance();

        profilePicture = findViewById(R.id.profileImage);
        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        });

        View root = findViewById(R.id.main);
        View scroll = findViewById(R.id.profileScroll);
        View bottomBar = findViewById(R.id.bottomBar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (scroll != null) scroll.setPadding(0, systemBars.top, 0, 0);
            if (bottomBar != null) bottomBar.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        loadProfileData();
        setupAccountSettings();
        setupSupport();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.PROFILE);

        // Edit profile button → Personal Information
        findViewById(R.id.editProfileBtn).setOnClickListener(v ->
                startActivity(new Intent(this, PersonalInformationActivity.class)));

        // Language selector
        findViewById(R.id.languageSelector).setOnClickListener(v -> showLanguageDialog());

        // Biometric switch
        SwitchCompat biometricSwitch = findViewById(R.id.biometricSwitch);
        biometricSwitch.setOnCheckedChangeListener((btn, isChecked) ->
                Toast.makeText(this, "Biometrics " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show());

        // Log out
        findViewById(R.id.logoutBtn).setOnClickListener(v -> showLogoutDialog());
    }

    private void loadProfileData() {
        String name = session.getFullName();
        String uid  = session.getUid();

        TextView tvName = findViewById(R.id.profileName);
        if (tvName != null && name != null && !name.isEmpty()) tvName.setText(name);

        TextView tvId = findViewById(R.id.patientId);
        if (tvId != null) {
            String safeUidPart = "";
            if (uid != null && !uid.isEmpty()) {
                safeUidPart = uid.substring(0, Math.min(uid.length(), 6)).toUpperCase();
            }
            tvId.setText(safeUidPart.isEmpty() ? "PN-N/A" : "PN-" + safeUidPart);
        }

        // Load extra fields from Firestore
        if (uid != null) {
            authRepo.getProfile(uid, new com.example.mugangaconnect.data.repository.AuthRepository.ProfileCallback() {
                @Override
                public void onSuccess(com.example.mugangaconnect.data.model.User user) {
                    runOnUiThread(() -> {
                        if (tvName != null && user.getFullName() != null) tvName.setText(user.getFullName());
                        String email = user.getEmail() != null ? user.getEmail() : session.getEmail();
                        session.saveSession(uid, user.getFullName(), email,
                                user.getPhone() != null ? user.getPhone() : "");
                    });
                }
                @Override public void onError(String message) {}
            });
            
            // Load profile image
            loadProfileImage(uid);
        }
    }

    private void loadProfileImage(String uid) {
        // First try to load from user's main document (most reliable)
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImageUrl = documentSnapshot.getString("profilePicture");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this).load(profileImageUrl).into(profilePicture);
                        } else {
                            // Fallback to user_images collection
                            loadProfileImageFromUserImages(uid);
                        }
                    } else {
                        // Fallback to user_images collection
                        loadProfileImageFromUserImages(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    // Fallback to user_images collection on error
                    loadProfileImageFromUserImages(uid);
                });
    }
    
    private void loadProfileImageFromUserImages(String uid) {
        firestore.collection("user_images")
                .whereEqualTo("userId", uid)
                .whereEqualTo("folder", "profile_images")
                .orderBy("uploadedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String imageUrl = task.getResult().getDocuments().get(0).getString("imageUrl");
                        if (imageUrl != null) {
                            Glide.with(ProfileActivity.this).load(imageUrl).into(profilePicture);
                        }
                    }
                });
    }

    private void setupAccountSettings() {
        findViewById(R.id.personalInfoItem).setOnClickListener(v ->
                startActivity(new Intent(this, PersonalInformationActivity.class)));

        findViewById(R.id.securityPinItem).setOnClickListener(v ->
                startActivity(new Intent(this, SecurityPinActivity.class)));

        findViewById(R.id.notificationItem).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationPreferencesActivity.class)));

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

    private void uploadProfileImage(Uri imageUri) {
        imageUploadUtils.uploadProfileImage(imageUri, new ImageUploadUtils.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                    Glide.with(ProfileActivity.this).load(imageUrl).into(profilePicture);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void setPendingImageCallback(ImagePickerUtils.ImagePickerCallback callback) {
        this.pendingImageCallback = callback;
    }

    @Override
    public ImagePickerUtils.ImagePickerCallback getPendingImageCallback() {
        return pendingImageCallback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadProfileImage(imageUri);
        }
    }
}
