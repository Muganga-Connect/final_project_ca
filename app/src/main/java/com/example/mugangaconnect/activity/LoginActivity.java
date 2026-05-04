package com.example.mugangaconnect;

import android.content.Context;
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

import com.example.mugangaconnect.data.repository.AuthRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private AuthRepository authRepo;
    private SessionManager session;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        authRepo = new AuthRepository();
        session  = new SessionManager(this);

        // Skip login if already authenticated
        if (session.isLoggedIn() && authRepo.isLoggedIn()) {
            goToDashboard();
            return;
        }

        setContentView(R.layout.login);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.authRoot), (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        EditText etEmail    = findViewById(R.id.etLoginEmail);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        ImageView ivToggle  = findViewById(R.id.ivPasswordToggle);
        Button btnLogin     = findViewById(R.id.btnLogin);
        LinearLayout btnBio = findViewById(R.id.btnBiometric);
        TextView tvForgot   = findViewById(R.id.tvForgotPassword);
        TextView tvSignUp   = findViewById(R.id.tvSignUpLink);
        LinearLayout tabSignUp = findViewById(R.id.tabSignUp);

        ivToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            ivToggle.setAlpha(isPasswordVisible ? 1.0f : 0.5f);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) { etEmail.setError("Please enter email"); return; }
            if (password.isEmpty()) { etPassword.setError("Please enter password"); return; }

            btnLogin.setEnabled(false);
            authRepo.login(email, password, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    authRepo.getProfile(user.getUid(), new AuthRepository.ProfileCallback() {
                        @Override
                        public void onSuccess(com.example.mugangaconnect.data.model.User profile) {
                            session.saveSession(user.getUid(), profile.getFullName(), profile.getEmail());
                            goToDashboard();
                        }
                        @Override
                        public void onError(String message) {
                            session.saveSession(user.getUid(), "", email);
                            goToDashboard();
                        }
                    });
                }
                @Override
                public void onError(String message) {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        tvSignUp.setOnClickListener(v -> goToSignUp());
        tabSignUp.setOnClickListener(v -> goToSignUp());

        // Forgot Password — sends a real reset email via Firebase
        tvForgot.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Enter your email first");
                etEmail.requestFocus();
                return;
            }
            authRepo.resetPassword(email, new AuthRepository.ResetCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(LoginActivity.this,
                            "Password reset link sent to " + email,
                            Toast.LENGTH_LONG).show();
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        btnBio.setOnClickListener(v ->
                Toast.makeText(this, "Biometric login coming soon", Toast.LENGTH_SHORT).show());
    }

    private void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}