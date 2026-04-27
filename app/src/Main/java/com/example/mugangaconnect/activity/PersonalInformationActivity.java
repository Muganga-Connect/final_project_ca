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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class PersonalInformationActivity extends AppCompatActivity {

    // Request Codes
    private static final int REQ_CAMERA = 101;
    private static final int REQ_GALLERY = 102;
    private static final int PERM_CAMERA = 201;
    private static final int PERM_STORAGE = 202;

    // UI Components
    private ImageView imgProfile;
    private MaterialButton btnEditPhoto, btnSave;
    private ImageButton btnBack;
    private TextView tvDisplayName;

    // Form Fields (using the included layout pattern)
    private View fieldName, fieldEmail, fieldPhone, fieldDob, fieldInsurance, fieldAllergies, fieldEmergency;
    private TextInputEditText etName, etEmail, etPhone, etDob, etInsurance, etAllergies, etEmergency;
    private ImageButton btnEditName, btnEditEmail, btnEditPhone, btnEditDob, btnEditInsurance, btnEditAllergies, btnEditEmergency;
    private TextView tvErrorName, tvErrorEmail, tvErrorPhone, tvErrorDob, tvErrorInsurance, tvErrorAllergies, tvErrorEmergency;

    // Spinners
    private Spinner spinnerGender, spinnerBlood;
    private ImageButton btnEditGender, btnEditBlood;
    private TextView tvErrorGender, tvErrorBlood;

    // State
    private boolean isModified = false;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "MugangaConnectPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        setupFields();
        setupSpinners();
        loadSavedData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        imgProfile = findViewById(R.id.img_profile);
        btnEditPhoto = findViewById(R.id.btn_edit_photo);
        btnSave = findViewById(R.id.btn_save_changes);
        tvDisplayName = findViewById(R.id.tv_display_name);

        // Map fields from included layouts
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
        // Set specific input types
        etName.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        etEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        etDob.setFocusable(false); // Date picker only
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

    private void loadSavedData() {
        etName.setText(prefs.getString("profile_fullName", "Alexandrine Mukamana"));
        etEmail.setText(prefs.getString("profile_email", "patient@example.com"));
        etPhone.setText(prefs.getString("profile_phone", "+250781234567"));
        etDob.setText(prefs.getString("profile_dob", "15 / Jan / 2000"));
        etInsurance.setText(prefs.getString("profile_insuranceId", "INS-2024-001234"));
        etAllergies.setText(prefs.getString("profile_allergies", "None"));
        etEmergency.setText(prefs.getString("profile_emergencyContact", "+250788654321"));

        tvDisplayName.setText(etName.getText().toString());

        setSelectionFromValue(spinnerGender, prefs.getString("profile_gender", "Female"));
        setSelectionFromValue(spinnerBlood, prefs.getString("profile_bloodType", "O+"));
    }

    private void setSelectionFromValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Profile Photo Edit
        btnEditPhoto.setOnClickListener(v -> showPhotoOptions());

        // Enable editing listeners
        btnEditName.setOnClickListener(v -> enableEditing(etName));
        btnEditEmail.setOnClickListener(v -> enableEditing(etEmail));
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

        // Modification trackers
        TextWatcher modificationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                markModified();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etName.addTextChangedListener(modificationWatcher);
        etEmail.addTextChangedListener(modificationWatcher);
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

        // Reset errors
        tvErrorName.setVisibility(View.GONE);
        tvErrorEmail.setVisibility(View.GONE);
        tvErrorPhone.setVisibility(View.GONE);
        tvErrorDob.setVisibility(View.GONE);
        tvErrorGender.setVisibility(View.GONE);
        tvErrorInsurance.setVisibility(View.GONE);
        tvErrorBlood.setVisibility(View.GONE);
        tvErrorAllergies.setVisibility(View.GONE);
        tvErrorEmergency.setVisibility(View.GONE);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String insurance = etInsurance.getText().toString().trim();
        String allergies = etAllergies.getText().toString().trim();
        String emergency = etEmergency.getText().toString().trim();

        if (name.length() < 3) {
            tvErrorName.setText("Name must be at least 3 characters");
            tvErrorName.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
            tvErrorEmail.setText("Invalid email format");
            tvErrorEmail.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!Pattern.matches("^\\+250\\d{9}$", phone)) {
            tvErrorPhone.setText("Must be +250 format with 9 digits");
            tvErrorPhone.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (dob.isEmpty()) {
            tvErrorDob.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (spinnerGender.getSelectedItemPosition() == 0) {
            tvErrorGender.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (insurance.isEmpty()) {
            tvErrorInsurance.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (spinnerBlood.getSelectedItemPosition() == 0) {
            tvErrorBlood.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (allergies.isEmpty()) {
            tvErrorAllergies.setText("Please specify allergies or write None");
            tvErrorAllergies.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!Pattern.matches("^\\+250\\d{9}$", emergency)) {
            tvErrorEmergency.setText("Must be valid phone number");
            tvErrorEmergency.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (isValid) {
            saveData();
        } else {
            Toast.makeText(this, "Please fix all errors", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profile_fullName", etName.getText().toString().trim());
        editor.putString("profile_email", etEmail.getText().toString().trim());
        editor.putString("profile_phone", etPhone.getText().toString().trim());
        editor.putString("profile_dob", etDob.getText().toString().trim());
        editor.putString("profile_gender", spinnerGender.getSelectedItem().toString());
        editor.putString("profile_insuranceId", etInsurance.getText().toString().trim());
        editor.putString("profile_bloodType", spinnerBlood.getSelectedItem().toString());
        editor.putString("profile_allergies", etAllergies.getText().toString().trim());
        editor.putString("profile_emergencyContact", etEmergency.getText().toString().trim());
        editor.apply();

        tvDisplayName.setText(etName.getText().toString().trim());
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

        // Lock fields again
        lockFields();
        isModified = false;
        btnSave.setEnabled(false);
    }

    private void lockFields() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
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
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}