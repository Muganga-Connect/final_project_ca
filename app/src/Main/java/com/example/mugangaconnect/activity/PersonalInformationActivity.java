package com.example.mugangaconnect.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.User;
import com.example.mugangaconnect.data.repository.AuthRepository;
import com.example.mugangaconnect.utils.ImageUploadUtils;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class PersonalInformationActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    private static final int REQ_CAMERA = 101;
    private static final int REQ_GALLERY = 102;
    private static final int PERM_CAMERA = 201;
    private static final int PERM_STORAGE = 202;

    private ImageView imgProfile;
    private MaterialButton btnEditPhoto, btnSave;
    private ImageButton btnBack;
    private TextView tvDisplayName, tvPatientId;

    private TextInputEditText etName, etEmail, etPhone, etDob, etInsurance, etAllergies, etEmergency, etWeight, etHeight;
    private ImageButton btnEditName, btnEditPhone, btnEditDob, btnEditInsurance, btnEditAllergies, btnEditEmergency, btnEditWeight, btnEditHeight;
    private TextView tvErrorName, tvErrorPhone;

    private Spinner spinnerGender, spinnerBlood;
    private ImageButton btnEditGender, btnEditBlood;

    private boolean isModified = false;
    private AuthRepository authRepo;
    private SessionManager session;
    private User currentUser;
    private ImageUploadUtils imageUploadUtils;
    private String newProfileImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        authRepo = new AuthRepository();
        session = new SessionManager(this);
        imageUploadUtils = new ImageUploadUtils(this);

        initViews();
        setupFields();
        setupSpinners();
        loadData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        imgProfile = findViewById(R.id.img_profile);
        btnEditPhoto = findViewById(R.id.btn_edit_photo);
        btnSave = findViewById(R.id.btn_save_changes);
        tvDisplayName = findViewById(R.id.tv_display_name);
        tvPatientId = findViewById(R.id.tv_patient_id);

        View fieldName = findViewById(R.id.field_full_name);
        View fieldEmail = findViewById(R.id.field_email);
        View fieldPhone = findViewById(R.id.field_phone);
        View fieldDob = findViewById(R.id.field_dob);
        View fieldInsurance = findViewById(R.id.field_insurance);
        View fieldAllergies = findViewById(R.id.field_allergies);
        View fieldEmergency = findViewById(R.id.field_emergency);
        View fieldWeight = findViewById(R.id.field_weight);
        View fieldHeight = findViewById(R.id.field_height);

        setupFieldInternal(fieldName, "FULL NAME", "e.g. Alexandrine Mukamana");
        setupFieldInternal(fieldEmail, "EMAIL ADDRESS", "e.g. patient@example.com");
        setupFieldInternal(fieldPhone, "PHONE NUMBER", "e.g. +250781234567");
        setupFieldInternal(fieldDob, "DATE OF BIRTH", "e.g. 15 / Jan / 2000");
        setupFieldInternal(fieldInsurance, "INSURANCE ID", "e.g. INS-2024-001234");
        setupFieldInternal(fieldAllergies, "MEDICAL ALLERGIES", "e.g. Penicillin, Peanuts or None");
        setupFieldInternal(fieldEmergency, "EMERGENCY CONTACT", "e.g. +250788654321");
        setupFieldInternal(fieldWeight, "WEIGHT (KG)", "e.g. 68.4");
        setupFieldInternal(fieldHeight, "HEIGHT (CM)", "e.g. 172");

        etName = fieldName.findViewById(R.id.et_input);
        etEmail = fieldEmail.findViewById(R.id.et_input);
        etPhone = fieldPhone.findViewById(R.id.et_input);
        etDob = fieldDob.findViewById(R.id.et_input);
        etInsurance = fieldInsurance.findViewById(R.id.et_input);
        etAllergies = fieldAllergies.findViewById(R.id.et_input);
        etEmergency = fieldEmergency.findViewById(R.id.et_input);
        etWeight = fieldWeight.findViewById(R.id.et_input);
        etHeight = fieldHeight.findViewById(R.id.et_input);

        btnEditName = fieldName.findViewById(R.id.btn_edit_field);
        btnEditPhone = fieldPhone.findViewById(R.id.btn_edit_field);
        btnEditDob = fieldDob.findViewById(R.id.btn_edit_field);
        btnEditInsurance = fieldInsurance.findViewById(R.id.btn_edit_field);
        btnEditAllergies = fieldAllergies.findViewById(R.id.btn_edit_field);
        btnEditEmergency = fieldEmergency.findViewById(R.id.btn_edit_field);
        btnEditWeight = fieldWeight.findViewById(R.id.btn_edit_field);
        btnEditHeight = fieldHeight.findViewById(R.id.btn_edit_field);

        tvErrorName = fieldName.findViewById(R.id.tv_error);
        tvErrorPhone = fieldPhone.findViewById(R.id.tv_error);

        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerBlood = findViewById(R.id.spinner_blood);
        btnEditGender = findViewById(R.id.btn_edit_gender);
        btnEditBlood = findViewById(R.id.btn_edit_blood);
    }

    private void setupFieldInternal(View field, String label, String hint) {
        ((TextView) field.findViewById(R.id.tv_label)).setText(label);
        ((TextInputEditText) field.findViewById(R.id.et_input)).setHint(hint);
    }

    private void setupFields() {
        etName.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        etEmail.setEnabled(false);
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        etDob.setFocusable(false);
        etDob.setClickable(false);
        etWeight.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etHeight.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
    }

    private void setupSpinners() {
        String[] genders = {"Select gender", "Male", "Female", "Other"};
        spinnerGender.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders));

        String[] bloodTypes = {"Select blood type", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        spinnerBlood.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodTypes));
    }

    private void loadData() {
        String uid = session.getUid();
        if (uid == null) return;

        authRepo.getProfile(uid, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                runOnUiThread(() -> {
                    tvDisplayName.setText(user.getFullName());
                    tvPatientId.setText("PN-" + user.getUid().substring(0, 5).toUpperCase());
                    etName.setText(user.getFullName());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    etDob.setText(user.getDob());
                    etInsurance.setText(user.getInsuranceId());
                    etAllergies.setText(user.getAllergies());
                    etEmergency.setText(user.getEmergencyContact());
                    etWeight.setText(user.getWeight());
                    etHeight.setText(user.getHeight());
                    setSelectionFromValue(spinnerGender, user.getGender());
                    setSelectionFromValue(spinnerBlood, user.getBloodType());
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(PersonalInformationActivity.this).load(user.getProfileImageUrl()).placeholder(R.drawable.user).into(imgProfile);
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(PersonalInformationActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setSelectionFromValue(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnEditPhoto.setOnClickListener(v -> showPhotoOptions());
        btnEditName.setOnClickListener(v -> enableEditing(etName));
        btnEditPhone.setOnClickListener(v -> enableEditing(etPhone));
        btnEditDob.setOnClickListener(v -> showDatePicker());
        btnEditInsurance.setOnClickListener(v -> enableEditing(etInsurance));
        btnEditAllergies.setOnClickListener(v -> enableEditing(etAllergies));
        btnEditEmergency.setOnClickListener(v -> enableEditing(etEmergency));
        btnEditWeight.setOnClickListener(v -> enableEditing(etWeight));
        btnEditHeight.setOnClickListener(v -> enableEditing(etHeight));

        btnEditGender.setOnClickListener(v -> { spinnerGender.setEnabled(true); spinnerGender.performClick(); markModified(); });
        btnEditBlood.setOnClickListener(v -> { spinnerBlood.setEnabled(true); spinnerBlood.performClick(); markModified(); });

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { markModified(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etInsurance.addTextChangedListener(watcher);
        etAllergies.addTextChangedListener(watcher);
        etEmergency.addTextChangedListener(watcher);
        etWeight.addTextChangedListener(watcher);
        etHeight.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void enableEditing(TextInputEditText et) {
        et.setEnabled(true);
        et.requestFocus();
        et.setSelection(et.getText().length());
        markModified();
    }

    private void markModified() {
        isModified = true;
        btnSave.setEnabled(true);
        btnSave.setAlpha(1.0f);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar sel = Calendar.getInstance(); sel.set(y, m, d);
            etDob.setText(new SimpleDateFormat("dd / MMM / yyyy", Locale.getDefault()).format(sel.getTime()));
            markModified();
        }, c.get(Calendar.YEAR)-20, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showPhotoOptions() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View v = getLayoutInflater().inflate(R.layout.layout_photo_bottom_sheet, null);
        v.findViewById(R.id.btn_take_photo).setOnClickListener(view -> { checkCameraPermission(); dialog.dismiss(); });
        v.findViewById(R.id.btn_choose_gallery).setOnClickListener(view -> { checkStoragePermission(); dialog.dismiss(); });
        v.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.setContentView(v);
        dialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERM_CAMERA);
        } else openCamera();
    }

    private void checkStoragePermission() {
        String perm = android.os.Build.VERSION.SDK_INT >= 33 ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{perm}, PERM_STORAGE);
        } else openGallery();
    }

    private void openCamera() { startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQ_CAMERA); }
    private void openGallery() { startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQ_GALLERY); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQ_CAMERA) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(bmp);
                uploadImage(Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "temp", null)));
            } else if (requestCode == REQ_GALLERY) {
                imgProfile.setImageURI(data.getData());
                uploadImage(data.getData());
            }
        }
    }

    private void uploadImage(Uri uri) {
        imageUploadUtils.uploadProfileImage(uri, new ImageUploadUtils.UploadCallback() {
            @Override public void onSuccess(String url) { newProfileImageUrl = url; runOnUiThread(() -> markModified()); }
            @Override public void onError(String err) { runOnUiThread(() -> Toast.makeText(PersonalInformationActivity.this, err, Toast.LENGTH_SHORT).show()); }
        });
    }

    private void validateAndSave() {
        if (currentUser == null) return;
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        if (name.isEmpty() || phone.isEmpty()) return;

        currentUser.setFullName(name);
        currentUser.setPhone(phone);
        currentUser.setDob(etDob.getText().toString().trim());
        currentUser.setInsuranceId(etInsurance.getText().toString().trim());
        currentUser.setAllergies(etAllergies.getText().toString().trim());
        currentUser.setEmergencyContact(etEmergency.getText().toString().trim());
        currentUser.setWeight(etWeight.getText().toString().trim());
        currentUser.setHeight(etHeight.getText().toString().trim());
        if (spinnerGender.getSelectedItemPosition() > 0) currentUser.setGender(spinnerGender.getSelectedItem().toString());
        if (spinnerBlood.getSelectedItemPosition() > 0) currentUser.setBloodType(spinnerBlood.getSelectedItem().toString());
        if (newProfileImageUrl != null) currentUser.setProfileImageUrl(newProfileImageUrl);

        btnSave.setEnabled(false);
        authRepo.updateFullProfile(currentUser, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                session.saveSession(user.uid, user.fullName, user.email, user.phone);
                runOnUiThread(() -> {
                    tvDisplayName.setText(user.fullName);
                    Toast.makeText(PersonalInformationActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
            @Override public void onError(String msg) { runOnUiThread(() -> { btnSave.setEnabled(true); Toast.makeText(PersonalInformationActivity.this, msg, Toast.LENGTH_SHORT).show(); }); }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERM_CAMERA) openCamera();
            if (requestCode == PERM_STORAGE) openGallery();
        }
    }
}