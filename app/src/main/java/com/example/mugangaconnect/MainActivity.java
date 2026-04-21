package com.example.mugangaconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main);
        View scroll = findViewById(R.id.dashboardScroll);
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

        // Initialize dashboard navigation
        initDashboardNavigation();
    }

    private void initDashboardNavigation() {
        // Bottom navigation handling
        LinearLayout dashboardNav = findNavigationItem("dashboard");
        LinearLayout scheduleNav = findNavigationItem("schedule");
        LinearLayout aiAssistantNav = findNavigationItem("ai_assistant");
        LinearLayout profileNav = findNavigationItem("profile");

        if (dashboardNav != null) {
            dashboardNav.setOnClickListener(v -> {
                // Already on dashboard - no action needed
            });
        }

        if (scheduleNav != null) {
            scheduleNav.setOnClickListener(v -> {
                Toast.makeText(this, "Schedule feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }

        if (aiAssistantNav != null) {
            aiAssistantNav.setOnClickListener(v -> {
                startActivity(new Intent(this, AIAssistantActivity.class));
            });
        }

        if (profileNav != null) {
            profileNav.setOnClickListener(v -> {
                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private LinearLayout findNavigationItem(String tag) {
        View bottomBar = findViewById(R.id.bottomBar);
        if (bottomBar instanceof LinearLayout) {
            LinearLayout navContainer = (LinearLayout) bottomBar;
            for (int i = 0; i < navContainer.getChildCount(); i++) {
                View child = navContainer.getChildAt(i);
                if (tag.equals(child.getTag())) {
                    return (LinearLayout) child;
                }
            }
        }
        return null;
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }
}