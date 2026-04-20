package com.example.mugangaconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.signupRoot), (v, insets) -> {
                    Insets sys = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        // Views
        EditText etFullName = findViewById(R.id.etFullName);
        EditText etEmail = findViewById(R.id.etSignUpEmail);
        EditText etPassword = findViewById(R.id.etSignUpPassword);
        ImageView ivPwdToggle = findViewById(R.id.ivPasswordToggle);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        LinearLayout btnBio = findViewById(R.id.btnBiometric);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
        LinearLayout tabLogin = findViewById(R.id.tabLogin);
        
        LinearLayout layoutStrength = findViewById(R.id.layoutPasswordStrength);
        ProgressBar pbStrength = findViewById(R.id.pbStrength);
        TextView tvStrengthLabel = findViewById(R.id.tvStrengthLabel);

        // Password visibility toggle
        ivPwdToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            ivPwdToggle.setImageResource(isPasswordVisible ? R.drawable.ic_eye : R.drawable.ic_eye); // Assuming same icon or change if needed
            etPassword.setSelection(etPassword.getText().length());
        });

        // Password Strength Logic
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && etPassword.getText().length() > 0) {
                layoutStrength.setVisibility(View.VISIBLE);
            } else {
                layoutStrength.setVisibility(View.GONE);
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    layoutStrength.setVisibility(View.VISIBLE);
                    updateStrengthIndicator(s.toString(), pbStrength, tvStrengthLabel);
                } else {
                    layoutStrength.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sign Up button
        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullName.isEmpty()) {
                etFullName.setError("Enter your full name");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Enter email or phone");
                return;
            }
            if (password.length() < 6) {
                etPassword.setError("Password too weak");
                return;
            }

            Toast.makeText(this, "Account created for " + fullName, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Biometrics
        if (btnBio != null) {
            btnBio.setOnClickListener(v ->
                    Toast.makeText(this, "Biometric registration coming soon", Toast.LENGTH_SHORT).show()
            );
        }

        tvLoginLink.setOnClickListener(v -> goToLogin());
        tabLogin.setOnClickListener(v -> goToLogin());
    }

    private void updateStrengthIndicator(String password, ProgressBar pb, TextView label) {
        if (password.length() < 6) {
            pb.setProgress(30);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFF4444)); // Red
            label.setText("WEAK");
        } else if (password.length() < 10) {
            pb.setProgress(60);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFFA500)); // Orange
            label.setText("MEDIUM");
        } else {
            pb.setProgress(100);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); // Green
            label.setText("STRONG");
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}