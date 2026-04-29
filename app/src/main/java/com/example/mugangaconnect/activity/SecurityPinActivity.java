package com.example.mugangaconnect;

import android.os.Bundle;
import android.text.InputFilter;
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
