package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AIAssistantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_ai_assistant);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.topStatsBar), (v, insets) -> {
                    Insets sys = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        // Initialize AI Assistant UI components
        initAIAssistantUI();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.AI_ASSISTANT);
    }

    private void initAIAssistantUI() {
        // Quick action buttons
        TextView checkRiskBtn = findViewById(R.id.quickActionsLayout)
                .findViewWithTag("check_risk");
        TextView rescheduleBtn = findViewById(R.id.quickActionsLayout)
                .findViewWithTag("reschedule");

        if (checkRiskBtn != null) {
            checkRiskBtn.setOnClickListener(v -> {
                Toast.makeText(this, "Analyzing appointment risk...", Toast.LENGTH_SHORT).show();
                // TODO: Implement risk analysis logic
            });
        }

        if (rescheduleBtn != null) {
            rescheduleBtn.setOnClickListener(v -> {
                Toast.makeText(this, "Opening appointment scheduler...", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to schedule screen
            });
        }

        // Message input
        EditText messageInput = findViewById(R.id.inputCard)
                .findViewById(android.R.id.edit);
        ImageView sendButton = findViewById(R.id.inputCard)
                .findViewById(android.R.id.button1);

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageInput.setText("");
                }
            });
        }
    }

    private void sendMessage(String message) {
        // TODO: Implement AI message sending logic
        Toast.makeText(this, "Sending: " + message, Toast.LENGTH_SHORT).show();
    }
}
