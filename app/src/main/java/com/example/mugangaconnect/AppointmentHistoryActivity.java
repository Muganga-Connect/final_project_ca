package com.example.mugangaconnect;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AppointmentHistoryActivity extends AppCompatActivity {

    private MaterialCardView tabCompleted, tabCancelled, tabMissed;
    private TextView textCompleted, textCancelled, textMissed;
    private MaterialCardView card1, card2, card3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_history);

        // Initialize Tabs
        tabCompleted = findViewById(R.id.tabCompleted);
        tabCancelled = findViewById(R.id.tabCancelled);
        tabMissed = findViewById(R.id.tabMissed);

        textCompleted = findViewById(R.id.textCompleted);
        textCancelled = findViewById(R.id.textCancelled);
        textMissed = findViewById(R.id.textMissed);

        // Initialize Cards
        card1 = findViewById(R.id.card1); // Completed
        card2 = findViewById(R.id.card2); // Cancelled
        card3 = findViewById(R.id.card3); // Missed

        // Tab Click Listeners
        tabCompleted.setOnClickListener(v -> selectTab("completed"));
        tabCancelled.setOnClickListener(v -> selectTab("cancelled"));
        tabMissed.setOnClickListener(v -> selectTab("missed"));

        // Back button functionality
        ImageView backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        // More button functionality
        MaterialCardView moreBtn = findViewById(R.id.moreBtn);
        if (moreBtn != null) {
            moreBtn.setOnClickListener(v -> Toast.makeText(this, "Loading more appointments...", Toast.LENGTH_SHORT).show());
        }
    }

    private void selectTab(String status) {
        // Reset all tabs to unselected state
        resetTabs();

        // Hide all cards initially
        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);

        switch (status) {
            case "completed":
                updateTabStyle(tabCompleted, textCompleted, true);
                card1.setVisibility(View.VISIBLE);
                break;
            case "cancelled":
                updateTabStyle(tabCancelled, textCancelled, true);
                card2.setVisibility(View.VISIBLE);
                break;
            case "missed":
                updateTabStyle(tabMissed, textMissed, true);
                card3.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void resetTabs() {
        updateTabStyle(tabCompleted, textCompleted, false);
        updateTabStyle(tabCancelled, textCancelled, false);
        updateTabStyle(tabMissed, textMissed, false);
    }

    private void updateTabStyle(MaterialCardView tab, TextView text, boolean isSelected) {
        if (isSelected) {
            tab.setCardBackgroundColor(Color.parseColor("#1A4C91"));
            text.setTextColor(Color.WHITE);
            text.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tab.setCardBackgroundColor(Color.WHITE);
            text.setTextColor(Color.parseColor("#6C96C3"));
            text.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }
}
