package com.example.mugangaconnect;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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

        // Set PIN input filters
        InputFilter[] pinFilter = new InputFilter[]{new InputFilter.LengthFilter(4)};
        etCurrentPin.setFilters(pinFilter);
        etNewPin.setFilters(pinFilter);
        etConfirmPin.setFilters(pinFilter);
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

        // Add text change listeners to clear errors
        etCurrentPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etCurrentPin.setError(null);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etNewPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etNewPin.setError(null);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etConfirmPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etConfirmPin.setError(null);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
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
        if (!validateCurrentPin(currentPin)) {
            return;
        }

        // Validate new PIN
        if (!validateNewPin(newPin)) {
            return;
        }

        // Validate confirmation PIN
        if (!validateConfirmPin(newPin, confirmPin)) {
            return;
        }

        // Process PIN change
        processPinChange();
    }

    /**
     * Validate current PIN input
     */
    private boolean validateCurrentPin(String currentPin) {
        if (TextUtils.isEmpty(currentPin)) {
            etCurrentPin.setError("Enter current PIN");
            return false;
        }

        if (currentPin.length() < 4) {
            etCurrentPin.setError("PIN must be 4 digits");
            return false;
        }

        return true;
    }

    /**
     * Validate new PIN input
     */
    private boolean validateNewPin(String newPin) {
        if (TextUtils.isEmpty(newPin)) {
            etNewPin.setError("Enter new PIN");
            return false;
        }

        if (newPin.length() < 4) {
            etNewPin.setError("PIN must be 4 digits");
            return false;
        }

        // Check if new PIN is same as current PIN
        if (newPin.equals(etCurrentPin.getText().toString().trim())) {
            etNewPin.setError("New PIN must be different from current PIN");
            return false;
        }

        return true;
    }

    /**
     * Validate confirmation PIN matches new PIN
     */
    private boolean validateConfirmPin(String newPin, String confirmPin) {
        if (TextUtils.isEmpty(confirmPin)) {
            etConfirmPin.setError("Please confirm your new PIN");
            return false;
        }

        if (!newPin.equals(confirmPin)) {
            etConfirmPin.setError("PINs do not match");
            Toast.makeText(this, "New PIN and Confirmation PIN do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Process the PIN change operation
     */
    private void processPinChange() {
        // TODO: In a real application, implement the following:
        // 1. Verify current PIN against stored PIN
        // 2. Hash the new PIN using secure encryption
        // 3. Save the new PIN to SharedPreferences or secure storage
        // 4. Clear any cached PIN data
        
        // Show success message
        showToast("PIN successfully updated!");
        
        // Clear input fields after successful update
        clearPinFields();
        
        // Optionally close activity or return to previous screen
        // finish();
    }

    /**
     * Show toast message
     * @param message Message to display
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Clear all PIN input fields
     */
    private void clearPinFields() {
        etCurrentPin.setText("");
        etNewPin.setText("");
        etConfirmPin.setText("");
    }
}
