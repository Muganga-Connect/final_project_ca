package com.example.mugangaconnect.activity;

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

import com.example.mugangaconnect.data.repository.AuthRepository;
import com.example.mugangaconnect.utils.SessionManager;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.activity.MainActivity;
import com.example.mugangaconnect.activity.LoginActivity;

public class SignUpActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private AuthRepository authRepo;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        authRepo = new AuthRepository();
        session  = new SessionManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.signupRoot), (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        EditText etFullName   = findViewById(R.id.etFullName);
        EditText etEmail      = findViewById(R.id.etSignUpEmail);
        EditText etPassword   = findViewById(R.id.etSignUpPassword);
        ImageView ivPwdToggle = findViewById(R.id.ivPasswordToggle);
        Button btnSignUp      = findViewById(R.id.btnSignUp);
        LinearLayout btnBio   = findViewById(R.id.btnBiometric);
        TextView tvLoginLink  = findViewById(R.id.tvLoginLink);
        LinearLayout tabLogin = findViewById(R.id.tabLogin);
        LinearLayout layoutStrength = findViewById(R.id.layoutPasswordStrength);
        ProgressBar pbStrength      = findViewById(R.id.pbStrength);
        TextView tvStrengthLabel    = findViewById(R.id.tvStrengthLabel);

        ivPwdToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            etPassword.setSelection(etPassword.getText().length());
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    layoutStrength.setVisibility(View.VISIBLE);
                    updateStrengthIndicator(s.toString(), pbStrength, tvStrengthLabel);
                } else {
                    layoutStrength.setVisibility(View.GONE);
                }
            }
        });

        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullName.isEmpty()) { etFullName.setError("Enter your full name"); return; }
            if (email.isEmpty())    { etEmail.setError("Enter email"); return; }
            if (password.length() < 6) { etPassword.setError("Password too weak"); return; }

            btnSignUp.setEnabled(false);
            authRepo.register(fullName, email, password, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    session.saveSession(user.getUid(), fullName, email);
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                }
                @Override
                public void onError(String message) {
                    btnSignUp.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        if (btnBio != null) {
            btnBio.setOnClickListener(v ->
                    Toast.makeText(this, "Biometric registration coming soon", Toast.LENGTH_SHORT).show());
        }

        tvLoginLink.setOnClickListener(v -> goToLogin());
        tabLogin.setOnClickListener(v -> goToLogin());
    }

    private void updateStrengthIndicator(String password, ProgressBar pb, TextView label) {
        if (password.length() < 6) {
            pb.setProgress(30);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFF4444));
            label.setText("WEAK");
        } else if (password.length() < 10) {
            pb.setProgress(60);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFFA500));
            label.setText("MEDIUM");
        } else {
            pb.setProgress(100);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            label.setText("STRONG");
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
