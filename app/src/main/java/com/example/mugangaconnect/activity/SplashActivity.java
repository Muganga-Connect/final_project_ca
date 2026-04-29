package com.example.mugangaconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mugangaconnect.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Connects to your activity_splash.xml layout
        setContentView(R.layout.activity_splash);

        // 3-second delay timer
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            com.example.mugangaconnect.utils.SessionManager session =
                    new com.example.mugangaconnect.utils.SessionManager(SplashActivity.this);
            com.example.mugangaconnect.data.repository.AuthRepository auth =
                    new com.example.mugangaconnect.data.repository.AuthRepository();
            Class<?> dest = (session.isLoggedIn() && auth.isLoggedIn())
                    ? MainActivity.class : LoginActivity.class;
            startActivity(new Intent(SplashActivity.this, dest));
            finish();
        }, 3000);
    }
}