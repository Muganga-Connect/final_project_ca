package com.example.mugangaconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mugangaconnect.R;

public class SplashActivity extends AppCompatActivity {

    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private final Runnable navigationRunnable = () -> {
        com.example.mugangaconnect.data.repository.AuthRepository auth =
                new com.example.mugangaconnect.data.repository.AuthRepository();
        Class<?> dest = auth.isLoggedIn()
                ? MainActivity.class : LoginActivity.class;
        startActivity(new Intent(SplashActivity.this, dest));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Connects to your activity_splash.xml layout
        setContentView(R.layout.activity_splash);

        splashHandler.postDelayed(navigationRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        splashHandler.removeCallbacks(navigationRunnable);
        super.onDestroy();
    }
}
