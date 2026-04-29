package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mugangaconnect.R;

/**
 * SecurityPinActivity - Handles PIN change functionality
 * Allows users to change their 4-digit security PIN
 * 
 * Features:
 * - Current PIN validation
 * - New PIN validation with strength checks
 * - Confirmation PIN matching
 * - Auto-clear validation errors on input
 * - Accessible UI with content descriptions
 */
public class SecurityPinActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MugangaConnectPrefs";
    private static final String KEY_PIN_HASH = "security_pin_hash";

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

        if (!isNumeric(currentPin)) {
            etCurrentPin.setError("PIN must contain only numbers");
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

        // Check for weak PINs (sequential or repeated digits)
        if (isWeakPin(newPin)) {
            etNewPin.setError("PIN is too weak. Choose a different PIN");
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
        String currentPin = etCurrentPin.getText().toString().trim();
        String newPin = etNewPin.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String storedHash = prefs.getString(KEY_PIN_HASH, "");
        String currentHash = String.valueOf(currentPin.hashCode());

        if (!storedHash.isEmpty() && !storedHash.equals(currentHash)) {
            etCurrentPin.setError("Current PIN is incorrect");
            showToast("Current PIN is incorrect");
            return;
        }

        prefs.edit().putString(KEY_PIN_HASH, String.valueOf(newPin.hashCode())).apply();
        showToast("PIN successfully updated!");
        clearPinFields();
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

    /**
     * Check if string contains only numeric characters
     * @param str String to check
     * @return true if numeric, false otherwise
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if PIN is weak (repeated or sequential digits)
     * @param pin PIN to check
     * @return true if weak, false otherwise
     */
    private boolean isWeakPin(String pin) {
        if (pin == null || pin.length() != 4) {
            return false;
        }

        // Check for repeated digits (e.g., 1111, 2222)
        if (pin.charAt(0) == pin.charAt(1) && 
            pin.charAt(1) == pin.charAt(2) && 
            pin.charAt(2) == pin.charAt(3)) {
            return true;
        }

        // Check for sequential digits (e.g., 1234, 4321)
        boolean ascending = true;
        boolean descending = true;
        for (int i = 0; i < pin.length() - 1; i++) {
            if (pin.charAt(i + 1) - pin.charAt(i) != 1) {
                ascending = false;
            }
            if (pin.charAt(i) - pin.charAt(i + 1) != 1) {
                descending = false;
            }
        }
        return ascending || descending;
    }
}
