package com.example.mugangaconnect;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SecurityPinActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etCurrentPin, etNewPin, etConfirmPin;
    private Button btnChangePin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_pin);
        initializeViews();
        setupClickListeners();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnChangePin.setOnClickListener(v -> processPinChange());

        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etCurrentPin.setError(null);
                etNewPin.setError(null);
                etConfirmPin.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etCurrentPin.addTextChangedListener(clearErrorWatcher);
        etNewPin.addTextChangedListener(clearErrorWatcher);
        etConfirmPin.addTextChangedListener(clearErrorWatcher);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etCurrentPin = findViewById(R.id.etCurrentPin);
        etNewPin = findViewById(R.id.etNewPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        btnChangePin = findViewById(R.id.btnChangePin);

        InputFilter[] pinFilter = new InputFilter[]{new InputFilter.LengthFilter(4)};
        etCurrentPin.setFilters(pinFilter);
        etNewPin.setFilters(pinFilter);
        etConfirmPin.setFilters(pinFilter);
    }
}
