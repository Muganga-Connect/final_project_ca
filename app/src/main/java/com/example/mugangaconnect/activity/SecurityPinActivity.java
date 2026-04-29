package com.example.mugangaconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecurityPinActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MugangaConnectPrefs";
    private static final String KEY_PIN_HASH = "security_pin_hash";

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

    private void processPinChange() {
        String currentPin = etCurrentPin.getText().toString().trim();
        String newPin = etNewPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        if (TextUtils.isEmpty(currentPin) || currentPin.length() < 4) {
            etCurrentPin.setError("Enter 4-digit current PIN");
            return;
        }
        if (TextUtils.isEmpty(newPin) || newPin.length() < 4) {
            etNewPin.setError("Enter 4-digit new PIN");
            return;
        }
        if (!newPin.equals(confirmPin)) {
            etConfirmPin.setError("PINs do not match");
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String storedHash = prefs.getString(KEY_PIN_HASH, null);
        if (storedHash != null) {
            String currentHash = String.valueOf(currentPin.hashCode());
            if (!currentHash.equals(storedHash)) {
                etCurrentPin.setError("Incorrect current PIN");
                return;
            }
        }

        String newHash = String.valueOf(newPin.hashCode());
        prefs.edit().putString(KEY_PIN_HASH, newHash).apply();
        Toast.makeText(this, "PIN successfully updated!", Toast.LENGTH_SHORT).show();
        etCurrentPin.setText("");
        etNewPin.setText("");
        etConfirmPin.setText("");
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
