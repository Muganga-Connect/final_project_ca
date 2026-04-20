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

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.authRoot), (v, insets) -> {
                    Insets sys = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        EditText etEmail = findViewById(R.id.etLoginEmail);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        ImageView ivToggle = findViewById(R.id.ivPasswordToggle);
        Button btnLogin = findViewById(R.id.btnLogin);
        LinearLayout btnBio = findViewById(R.id.btnBiometric);
        TextView tvForgot = findViewById(R.id.tvForgotPassword);
        TextView tvSignUp = findViewById(R.id.tvSignUpLink);
        LinearLayout tabSignUp = findViewById(R.id.tabSignUp);
        LinearLayout tabLogin = findViewById(R.id.tabLogin);

        // Password toggle
        ivToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            ivToggle.setAlpha(isPasswordVisible ? 1.0f : 0.5f);
            etPassword.setSelection(etPassword.getText().length());
        });

        // Login button go to Dashboard
        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Please enter email or phone");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Please enter password");
                return;
            }
            // Navigate to Dashboard
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Sign up tab or link go to SignUpActivity
        tvSignUp.setOnClickListener(v -> goToSignUp());
        tabSignUp.setOnClickListener(v -> goToSignUp());

        // Forgot password
        tvForgot.setOnClickListener(v ->
                Toast.makeText(this,
                        "Password reset link sent", Toast.LENGTH_SHORT).show()
        );

        // Biometrics
        btnBio.setOnClickListener(v ->
                Toast.makeText(this,
                        "Biometric login coming soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void goToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}