package com.example.mugangaconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

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
        EditText etConfirmPwd = findViewById(R.id.etConfirmPassword);
        ImageView ivPwdToggle = findViewById(R.id.ivPasswordToggle);
        ImageView ivConfirmToggle = findViewById(R.id.ivConfirmPasswordToggle);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        LinearLayout btnBio = findViewById(R.id.btnBiometric);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
        LinearLayout tabLogin = findViewById(R.id.tabLogin);

        // Password toggle
        ivPwdToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            ivPwdToggle.setAlpha(isPasswordVisible ? 1.0f : 0.5f);
            etPassword.setSelection(etPassword.getText().length());
        });

        // Confirm password toggle
        ivConfirmToggle.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            etConfirmPwd.setTransformationMethod(isConfirmPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            ivConfirmToggle.setAlpha(isConfirmPasswordVisible ? 1.0f : 0.5f);
            etConfirmPwd.setSelection(etConfirmPwd.getText().length());
        });

        // Sign Up button
        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPwd = etConfirmPwd.getText().toString().trim();

            if (fullName.isEmpty()) {
                etFullName.setError("Enter your full name");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Enter email or phone");
                return;
            }
            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }
            if (!password.equals(confirmPwd)) {
                etConfirmPwd.setError("Passwords do not match");
                return;
            }

            Toast.makeText(this,
                    "Account created for " + fullName,
                    Toast.LENGTH_SHORT).show();

            // Go to Dashboard after signup
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Biometrics
        if (btnBio != null) {
            btnBio.setOnClickListener(v ->
                    Toast.makeText(this,
                            "Biometric registration coming soon",
                            Toast.LENGTH_SHORT).show()
            );
        }

        // Back to Login both tab and link
        tvLoginLink.setOnClickListener(v -> goToLogin());
        tabLogin.setOnClickListener(v -> goToLogin());
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}