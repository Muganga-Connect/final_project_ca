package com.example.mugangaconnect;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SecurityPinActivity - Handles PIN change functionality
 * Allows users to change their 4-digit security PIN
 */
public class SecurityPinActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private EditText etCurrentPin, etNewPin, etConfirmPin;
    private Button btnChangePin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_pin);

        // Initialize UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Initialize all UI views
     */
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etCurrentPin = findViewById(R.id.etCurrentPin);
        etNewPin = findViewById(R.id.etNewPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        btnChangePin = findViewById(R.id.btnChangePin);
    }

    /**
     * Setup all click listeners
     */
    private void setupClickListeners() {
        // Back button functionality
        btnBack.setOnClickListener(v -> finish());

        // Change PIN button functionality
        btnChangePin.setOnClickListener(v -> {
            validateAndChangePin();
        });
    }

    /**
     * Validate PIN inputs and process PIN change
     */
    private void validateAndChangePin() {
        String currentPin = etCurrentPin.getText().toString().trim();
        String newPin = etNewPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        // Validate current PIN
        if (TextUtils.isEmpty(currentPin)) {
            etCurrentPin.setError("Enter current PIN");
            return;
        }

        if (currentPin.length() < 4) {
            etCurrentPin.setError("PIN must be 4 digits");
            return;
        }

        // Validate new PIN
        if (TextUtils.isEmpty(newPin)) {
            etNewPin.setError("Enter new PIN");
            return;
        }

        if (newPin.length() < 4) {
            etNewPin.setError("PIN must be 4 digits");
            return;
        }

        // Validate confirmation PIN
        if (TextUtils.isEmpty(confirmPin)) {
            etConfirmPin.setError("Please confirm your new PIN");
            return;
        }

        if (!newPin.equals(confirmPin)) {
            etConfirmPin.setError("PINs do not match");
            Toast.makeText(this, "New PIN and Confirmation PIN do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Process PIN change
        // In a real application, you would verify the current PIN and save the new PIN securely
        Toast.makeText(this, "PIN successfully updated!", Toast.LENGTH_SHORT).show();
        
        // Clear input fields after successful update
        etCurrentPin.setText("");
        etNewPin.setText("");
        etConfirmPin.setText("");
        
        // Optionally close activity or return to previous screen
        // finish();
    }
}
