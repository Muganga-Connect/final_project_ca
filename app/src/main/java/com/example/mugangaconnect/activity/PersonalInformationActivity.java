package com.example.mugangaconnect.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.User;
import com.example.mugangaconnect.data.repository.AuthRepository;
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
    private TextView tvDisplayName;

    private View fieldName, fieldEmail, fieldPhone, fieldDob, fieldInsurance, fieldAllergies, fieldEmergency;
    private TextInputEditText etName, etEmail, etPhone, etDob, etInsurance, etAllergies, etEmergency;
    private ImageButton btnEditName, btnEditEmail, btnEditPhone, btnEditDob, btnEditInsurance, btnEditAllergies, btnEditEmergency;
    private TextView tvErrorName, tvErrorEmail, tvErrorPhone, tvErrorDob, tvErrorInsurance, tvErrorAllergies, tvErrorEmergency;

    private Spinner spinnerGender, spinnerBlood;
    private ImageButton btnEditGender, btnEditBlood;
    private TextView tvErrorGender, tvErrorBlood;

    private boolean isModified = false;
    private AuthRepository authRepo;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        authRepo = new AuthRepository();
        session = new SessionManager(this);

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

        fieldName = findViewById(R.id.field_full_name);
        fieldEmail = findViewById(R.id.field_email);
        fieldPhone = findViewById(R.id.field_phone);
        fieldDob = findViewById(R.id.field_dob);
        fieldInsurance = findViewById(R.id.field_insurance);
        fieldAllergies = findViewById(R.id.field_allergies);
        fieldEmergency = findViewById(R.id.field_emergency);

        setupFieldInternal(fieldName, "FULL NAME", "e.g. Alexandrine Mukamana");
        setupFieldInternal(fieldEmail, "EMAIL ADDRESS", "e.g. patient@example.com");
        setupFieldInternal(fieldPhone, "PHONE NUMBER", "e.g. +250781234567");
        setupFieldInternal(fieldDob, "DATE OF BIRTH", "e.g. 15 / Jan / 2000");
        setupFieldInternal(fieldInsurance, "INSURANCE ID", "e.g. INS-2024-001234");
        setupFieldInternal(fieldAllergies, "MEDICAL ALLERGIES", "e.g. Penicillin, Peanuts or None");
        setupFieldInternal(fieldEmergency, "EMERGENCY CONTACT", "e.g. +250788654321");

        etName = fieldName.findViewById(R.id.et_input);
        etEmail = fieldEmail.findViewById(R.id.et_input);
        etPhone = fieldPhone.findViewById(R.id.et_input);
        etDob = fieldDob.findViewById(R.id.et_input);
        etInsurance = fieldInsurance.findViewById(R.id.et_input);
        etAllergies = fieldAllergies.findViewById(R.id.et_input);
        etEmergency = fieldEmergency.findViewById(R.id.et_input);

        btnEditName = fieldName.findViewById(R.id.btn_edit_field);
        btnEditEmail = fieldEmail.findViewById(R.id.btn_edit_field);
        btnEditPhone = fieldPhone.findViewById(R.id.btn_edit_field);
        btnEditDob = fieldDob.findViewById(R.id.btn_edit_field);
        btnEditInsurance = fieldInsurance.findViewById(R.id.btn_edit_field);
        btnEditAllergies = fieldAllergies.findViewById(R.id.btn_edit_field);
        btnEditEmergency = fieldEmergency.findViewById(R.id.btn_edit_field);

        tvErrorName = fieldName.findViewById(R.id.tv_error);
        tvErrorEmail = fieldEmail.findViewById(R.id.tv_error);
        tvErrorPhone = fieldPhone.findViewById(R.id.tv_error);
        tvErrorDob = fieldDob.findViewById(R.id.tv_error);
        tvErrorInsurance = fieldInsurance.findViewById(R.id.tv_error);
        tvErrorAllergies = fieldAllergies.findViewById(R.id.tv_error);
        tvErrorEmergency = fieldEmergency.findViewById(R.id.tv_error);

        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerBlood = findViewById(R.id.spinner_blood);
        btnEditGender = findViewById(R.id.btn_edit_gender);
        btnEditBlood = findViewById(R.id.btn_edit_blood);
        tvErrorGender = findViewById(R.id.tv_error_gender);
        tvErrorBlood = findViewById(R.id.tv_error_blood);
    }

    private void setupFieldInternal(View field, String label, String hint) {
        ((TextView) field.findViewById(R.id.tv_label)).setText(label);
        ((TextInputEditText) field.findViewById(R.id.et_input)).setHint(hint);
    }

    private void setupFields() {
        etName.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        etEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setEnabled(false); // Email usually not changeable for auth
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        etDob.setFocusable(false);
        etDob.setClickable(false);
        etInsurance.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etAllergies.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etEmergency.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
    }

    private void setupSpinners() {
        String[] genders = {"Select gender", "Male", "Female", "Prefer not to say"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(genderAdapter);

        String[] bloodTypes = {"Select blood type", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodTypes);
        spinnerBlood.setAdapter(bloodAdapter);
    }

    private void loadData() {
        String uid = session.getUid();
        if (uid == null) return;

        authRepo.getProfile(uid, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    etName.setText(user.getFullName());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    // If your User model doesn't have these, you might need to extend it or use a separate collection
                    // For now, let's assume they are there or we use defaults
                    tvDisplayName.setText(user.getFullName());
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(PersonalInformationActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
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
        btnEditInsurance.setOnClickListener(v -> enableEditing(etInsurance));
        btnEditAllergies.setOnClickListener(v -> enableEditing(etAllergies));
        btnEditEmergency.setOnClickListener(v -> enableEditing(etEmergency));

        btnEditDob.setOnClickListener(v -> showDatePicker());
        btnEditGender.setOnClickListener(v -> {
            spinnerGender.setEnabled(true);
            spinnerGender.performClick();
            markModified();
        });
        btnEditBlood.setOnClickListener(v -> {
            spinnerBlood.setEnabled(true);
            spinnerBlood.performClick();
            markModified();
        });

        TextWatcher modificationWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { markModified(); }
            @Override public void afterTextChanged(Editable s) {}
        };

        etName.addTextChangedListener(modificationWatcher);
        etPhone.addTextChangedListener(modificationWatcher);
        etInsurance.addTextChangedListener(modificationWatcher);
        etAllergies.addTextChangedListener(modificationWatcher);
        etEmergency.addTextChangedListener(modificationWatcher);

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
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd / MMM / yyyy", Locale.getDefault());
            etDob.setText(sdf.format(selectedDate.getTime()));
            markModified();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showPhotoOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_photo_bottom_sheet, null);
        
        view.findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            checkCameraPermission();
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btn_choose_gallery).setOnClickListener(v -> {
            checkStoragePermission();
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERM_CAMERA);
        } else {
            openCamera();
        }
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_STORAGE);
        } else {
            openGallery();
        }
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQ_CAMERA);
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQ_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQ_CAMERA) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(image);
                markModified();
            } else if (requestCode == REQ_GALLERY) {
                Uri selectedImage = data.getData();
                imgProfile.setImageURI(selectedImage);
                markModified();
            }
        }
    }

    private void validateAndSave() {
        boolean isValid = true;

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.length() < 3) {
            tvErrorName.setText("Name too short");
            tvErrorName.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!Pattern.matches("^\\+?\\d{10,15}$", phone)) {
            tvErrorPhone.setText("Invalid phone number");
            tvErrorPhone.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (isValid) {
            saveData(name, phone);
        }
    }

    private void saveData(String name, String phone) {
        String uid = session.getUid();
        authRepo.updateProfile(uid, name, phone, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                session.saveSession(uid, name, user.getEmail());
                runOnUiThread(() -> {
                    tvDisplayName.setText(name);
                    Toast.makeText(PersonalInformationActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    lockFields();
                    isModified = false;
                    btnSave.setEnabled(false);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(PersonalInformationActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void lockFields() {
        etName.setEnabled(false);
        etPhone.setEnabled(false);
        etInsurance.setEnabled(false);
        etAllergies.setEnabled(false);
        etEmergency.setEnabled(false);
        spinnerGender.setEnabled(false);
        spinnerBlood.setEnabled(false);
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
