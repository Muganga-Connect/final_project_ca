package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mugangaconnect.BottomNavHelper;
import com.example.mugangaconnect.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main);
        View scroll = findViewById(R.id.nestedScrollView);
        View bottomBar = findViewById(R.id.bottomBar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (scroll != null) {
                scroll.setPadding(systemBars.left + dp(14), systemBars.top + dp(14), systemBars.right + dp(14), dp(120));
            }
            if (bottomBar != null) {
                bottomBar.setPadding(0, 0, 0, systemBars.bottom);
            }
            return insets;
        });

        BottomNavHelper.setup(this, BottomNavHelper.Screen.DASHBOARD);
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }
}